package com.mahasbr.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;

import jakarta.annotation.PreDestroy;

/**
 * Pulls establishment data from the registry APIs configured in registry_api_auth_config.
 *
 * <ul>
 * <li><b>Only active APIs are called</b>, by the nightly job ({@code app.registry-sync.cron},
 * default 02:00) or by "Run now" on the Registry API Settings page.</li>
 * <li><b>Incremental:</b> each run continues after {@code last_synced_to}, re-reading a few
 * overlap days to catch late entries. Re-read records update their existing row, so nothing
 * is duplicated and BRNs never change.</li>
 * <li><b>Resumable:</b> progress is saved after each 3-month window, so a failure part-way
 * through resumes from there next time instead of starting over.</li>
 * <li>One run per API at a time; status is shown on the settings page.</li>
 * </ul>
 * If the app runs on more than one server, only one of them should run the nightly job.
 */
@Service
public class RegistrySyncService {

	private static final Logger log = LoggerFactory.getLogger(RegistrySyncService.class);

	static final String STATUS_RUNNING = "RUNNING";
	static final String STATUS_SUCCESS = "SUCCESS";
	static final String STATUS_FAILED = "FAILED";

	private static final int WINDOW_MONTHS = 3;
	private static final int WINDOW_ATTEMPTS = 3;
	private static final long RETRY_DELAY_MS = 5_000;
	private static final int MAX_BACKFILL_BATCHES = 20_000;

	private final RegistryApiAuthConfigRepository configRepository;
	private final RegistryApiClient apiClient;
	private final RegistryRecordUpsertService upsertService;
	private final ApiLogService apiLogService;
	private final ObjectMapper objectMapper;
	private final ZoneId zone;
	private final int overlapDays;

	/** Ids of APIs being synced right now (shared by nightly and manual runs). */
	private final Set<Long> running = ConcurrentHashMap.newKeySet();
	private final AtomicInteger threadCounter = new AtomicInteger();
	private final ExecutorService manualRuns = Executors.newFixedThreadPool(2, task -> {
		Thread thread = new Thread(task, "registry-sync-" + threadCounter.incrementAndGet());
		thread.setDaemon(true);
		return thread;
	});

	public RegistrySyncService(RegistryApiAuthConfigRepository configRepository, RegistryApiClient apiClient,
			RegistryRecordUpsertService upsertService, ApiLogService apiLogService, ObjectMapper objectMapper,
			@Value("${app.registry-sync.zone:Asia/Kolkata}") String zone,
			@Value("${app.registry-sync.overlap-days:7}") int overlapDays) {
		this.configRepository = configRepository;
		this.apiClient = apiClient;
		this.upsertService = upsertService;
		this.apiLogService = apiLogService;
		this.objectMapper = objectMapper;
		this.zone = ZoneId.of(zone);
		this.overlapDays = Math.max(0, overlapDays);
	}

	/** Nightly: syncs every API that is switched on, one after another. */
	@Scheduled(cron = "${app.registry-sync.cron:0 0 2 * * *}", zone = "${app.registry-sync.zone:Asia/Kolkata}")
	public void syncAllActive() {
		var active = configRepository.findByActiveTrue();
		log.info("Nightly registry sync: {} active API(s)", active.size());
		for (RegistryApiAuthConfigEntity config : active) {
			runSync(config.getId(), "nightly");
		}
	}

	/** "Run now" from the settings page. Returns at once; the sync continues in the background. */
	public void startSync(Long id) {
		RegistryApiAuthConfigEntity config = configRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Registry API not found with ID: " + id));
		if (!Boolean.TRUE.equals(config.getActive())) {
			throw new IllegalArgumentException("Switch this API on before running it.");
		}
		if (config.getRegistry() == null) {
			throw new IllegalArgumentException("Link this API to a registry before running it.");
		}
		if (config.getUsername() == null || config.getUsername().isBlank() || config.getPassword() == null
				|| config.getPassword().isBlank()) {
			throw new IllegalArgumentException("Enter the login (username and password) before running it.");
		}
		if (isRunning(id)) {
			throw new IllegalArgumentException("A sync for this API is already running.");
		}
		manualRuns.submit(() -> runSync(id, "manual"));
	}

	public boolean isRunning(Long id) {
		return id != null && running.contains(id);
	}

	void runSync(Long id, String trigger) {
		if (!running.add(id)) {
			log.info("Registry API {} is already syncing; skipping {} run", id, trigger);
			return;
		}
		String apiName = "registry-api-" + id;
		String dataUrl = null;
		try {
			RegistryApiAuthConfigEntity config = configRepository.findById(id).orElse(null);
			if (config == null || !Boolean.TRUE.equals(config.getActive())) {
				return;   // deleted or switched off since the run was requested
			}
			if (config.getRegistry() == null) {
				throw new IllegalStateException("No registry is linked to this API.");
			}
			apiName = config.getServiceName();
			dataUrl = config.getApiData();
			Integer regUserId = Math.toIntExact(config.getRegistry().getId());

			setStatus(id, STATUS_RUNNING, "Started (" + trigger + ")");
			backfillKeys(regUserId);

			LocalDate end = LocalDate.now(zone).minusDays(1);   // only complete days
			LocalDate start = startDate(config);
			if (start.isAfter(end)) {
				setStatus(id, STATUS_SUCCESS, "Already up to date (synced to " + config.getLastSyncedTo() + ").");
				return;
			}

			String token = apiClient.login(config);
			Counts total = new Counts();

			for (LocalDate from = start; !from.isAfter(end); ) {
				LocalDate to = min(from.plusMonths(WINDOW_MONTHS).minusDays(1), end);
				setStatus(id, STATUS_RUNNING, "Fetching " + from + " to " + to + " (" + total + " so far)");

				FetchResult result = fetchWindowWithRetry(config, token, from, to);
				token = result.token();
				Counts window = saveRecords(result, config, regUserId);
				total.add(window);

				configRepository.updateLastSyncedTo(id, to);   // progress survives a later failure
				apiLogService.log(apiName, dataUrl, STATUS_SUCCESS,
						"Window " + from + " to " + to + ": " + window);
				from = to.plusDays(1);
			}

			String message = "Synced " + start + " to " + end + ": " + total;
			setStatus(id, STATUS_SUCCESS, message);
			log.info("Registry sync {} ({}) finished: {}", apiName, trigger, message);
		} catch (Exception e) {
			String message = "Failed: " + rootMessage(e);
			log.error("Registry sync {} ({}) failed", apiName, trigger, e);
			setStatus(id, STATUS_FAILED, message);
			apiLogService.log(apiName, dataUrl, STATUS_FAILED, message);
		} finally {
			running.remove(id);
		}
	}

	/** First run: from sync_from_date. Later runs: from last_synced_to minus the overlap days. */
	LocalDate startDate(RegistryApiAuthConfigEntity config) {
		LocalDate syncFrom = config.getSyncFromDate() != null
				? config.getSyncFromDate()
				: RegistryApiAuthConfigServiceImpl.DEFAULT_SYNC_FROM;
		if (config.getLastSyncedTo() == null) {
			return syncFrom;
		}
		LocalDate resume = config.getLastSyncedTo().plusDays(1).minusDays(overlapDays);
		return resume.isBefore(syncFrom) ? syncFrom : resume;
	}

	private void backfillKeys(Integer regUserId) {
		int batches = 0;
		int updated;
		do {
			updated = upsertService.backfillBatch(regUserId);
			if (++batches > MAX_BACKFILL_BATCHES) {
				throw new IllegalStateException("Preparing existing records did not finish; check the logs.");
			}
		} while (updated >= RegistryRecordUpsertService.BACKFILL_BATCH_SIZE);
	}

	private FetchResult fetchWindowWithRetry(RegistryApiAuthConfigEntity config, String token, LocalDate from,
			LocalDate to) throws InterruptedException {
		String currentToken = token;
		boolean reloggedIn = false;
		for (int attempt = 1; ; attempt++) {
			try {
				return new FetchResult(apiClient.fetchWindow(config, currentToken, from, to), currentToken);
			} catch (HttpClientErrorException.Unauthorized e) {
				if (reloggedIn) {
					throw e;
				}
				currentToken = apiClient.login(config);   // token expired during a long run
				reloggedIn = true;
			} catch (ResourceAccessException | HttpServerErrorException e) {
				if (attempt >= WINDOW_ATTEMPTS) {
					throw e;
				}
				log.warn("Fetching {} {} to {} failed (attempt {}/{}): {}", config.getServiceName(), from, to,
						attempt, WINDOW_ATTEMPTS, e.getMessage());
				Thread.sleep(RETRY_DELAY_MS * attempt);
			}
		}
	}

	private Counts saveRecords(FetchResult result, RegistryApiAuthConfigEntity config, Integer regUserId) {
		Counts counts = new Counts();
		for (JsonNode node : result.records()) {
			MstRegistryDetailsPagesDTO dto = null;
			try {
				dto = objectMapper.treeToValue(node, MstRegistryDetailsPagesDTO.class);
				if (upsertService.upsert(dto, regUserId) == RegistryRecordUpsertService.Outcome.CREATED) {
					counts.created++;
				} else {
					counts.updated++;
				}
			} catch (Exception e) {
				counts.failed++;
				upsertService.saveFailed(dto, node.toString(), regUserId, rootMessage(e), config.getServiceName(),
						config.getApiData());
			}
		}
		return counts;
	}

	private void setStatus(Long id, String status, String message) {
		try {
			configRepository.updateRunStatus(id, status, truncate(message, 1000), LocalDateTime.now(zone));
		} catch (Exception e) {
			log.warn("Could not save sync status for registry API {}: {}", id, e.getMessage());
		}
	}

	private static LocalDate min(LocalDate a, LocalDate b) {
		return a.isBefore(b) ? a : b;
	}

	private static String rootMessage(Throwable e) {
		Throwable root = e;
		while (root.getCause() != null && root.getCause() != root) {
			root = root.getCause();
		}
		String message = root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
		return truncate(message.replaceAll("\\s+", " "), 900);
	}

	private static String truncate(String value, int max) {
		return value == null || value.length() <= max ? value : value.substring(0, max);
	}

	@PreDestroy
	void shutdown() {
		manualRuns.shutdownNow();
	}

	private record FetchResult(java.util.List<JsonNode> records, String token) {
	}

	/** Running totals for a window or a whole run. */
	static final class Counts {
		int created;
		int updated;
		int failed;

		void add(Counts other) {
			created += other.created;
			updated += other.updated;
			failed += other.failed;
		}

		@Override
		public String toString() {
			return created + " new, " + updated + " updated, " + failed + " failed";
		}
	}
}
