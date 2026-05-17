package com.mahasbr.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.CsvUploadFailedRecordDto;
import com.mahasbr.dto.CsvUploadParsedRecordDto;
import com.mahasbr.validator.CsvUploadValidator;
import com.mahasbr.entity.CsvUploadJobEntity;
import com.mahasbr.entity.User;
import com.mahasbr.repository.CsvUploadJobRepository;
import com.mahasbr.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvUploadAsyncProcessor {

	private static final int BATCH_SIZE = 1000;

	private final CsvUploadStorageService storageService;
	private final CsvUploadJobRepository jobRepository;
	private final CsvUploadBatchPersistenceService batchPersistenceService;
	private final UserRepository userRepository;
	private final CsvUploadValidator validator;
	private final Set<String> runningJobs = ConcurrentHashMap.newKeySet();

	@Async("mvcTaskExecutor")
	public void process(String jobId, String username) {
		if (!runningJobs.add(jobId)) {
			log.info("Skipping duplicate processor launch for jobId={}", jobId);
			return;
		}

		try {
			CsvUploadJobEntity initialJob = getRequiredJob(jobId);
			if (initialJob.getStatus() != CsvUploadLifecycleStatus.PROCESSING) {
				return;
			}

			Integer regUserId = resolveRegistryUserId(username);
			List<CsvUploadParsedRecordDto> batch = new ArrayList<>(BATCH_SIZE);
			AtomicInteger seenValidRows = new AtomicInteger();
			int startOffset = initialJob.getLastProcessedValidRecord();
			CsvUploadValidator.CsvUploadValidationContext validationContext = validator.prepareValidationContext();

			try {
				if (startOffset == 0) {
					persistPreviewFailures(jobId);
				}

				storageService.streamJsonLines(storageService.validRowsPath(jobId), CsvUploadParsedRecordDto.class, parsedRecord -> {
					if (seenValidRows.getAndIncrement() < startOffset) {
						return;
					}

					batch.add(parsedRecord);
					if (batch.size() >= BATCH_SIZE) {
						persistBatch(jobId, regUserId, initialJob.getFileName(), batch, validationContext);
						batch.clear();
						handleStopRequest(jobId);
					}
				});

				if (!batch.isEmpty()) {
					persistBatch(jobId, regUserId, initialJob.getFileName(), batch, validationContext);
					batch.clear();
				}

				handleStopRequest(jobId);
				markCompleted(jobId);
			} catch (StopProcessingSignal ignored) {
				log.info("Processing loop stopped for jobId={} due to status transition.", jobId);
			} catch (Exception exception) {
				log.error("CSV upload processing failed for jobId={}", jobId, exception);
				markFailed(jobId, exception);
			}
		} finally {
			runningJobs.remove(jobId);
		}
	}

	private void persistPreviewFailures(String jobId) throws IOException {
		List<CsvUploadFailedRecordDto> batch = new ArrayList<>(BATCH_SIZE);
		storageService.streamJsonLines(storageService.failedPreviewRowsPath(jobId), CsvUploadFailedRecordDto.class, failedRecord -> {
			batch.add(failedRecord);
			if (batch.size() >= BATCH_SIZE) {
				batchPersistenceService.savePreviewFailedRecords(jobId, List.copyOf(batch));
				batch.clear();
			}
		});

		if (!batch.isEmpty()) {
			batchPersistenceService.savePreviewFailedRecords(jobId, List.copyOf(batch));
		}
	}

	private void persistBatch(String jobId, Integer regUserId, String fileName, List<CsvUploadParsedRecordDto> batch,
			CsvUploadValidator.CsvUploadValidationContext validationContext) {
		List<CsvUploadParsedRecordDto> persistableBatch = new ArrayList<>(batch.size());
		int validationFailures = 0;
		Set<String> seenActKeys = new HashSet<>();
		Set<String> seenGstKeys = new HashSet<>();

		for (CsvUploadParsedRecordDto parsedRecord : batch) {
			var validation = validator.validateForProcessing(parsedRecord.getRowData(), validationContext);
			if (!validation.isValid()) {
				validationFailures++;
				batchPersistenceService.saveFailedRecord(jobId, parsedRecord, null, validation.getErrorMessage(), fileName);
				continue;
			}

			String actKey = buildDuplicateKey(parsedRecord.getRowData(), CsvUploadValidator.ACT_REGISTRATION_NO,
					CsvUploadValidator.NAME_OF_ACT);
			if (!actKey.isBlank() && !seenActKeys.add(actKey)) {
				validationFailures++;
				batchPersistenceService.saveFailedRecord(jobId, parsedRecord, null,
						"Duplicate record found for Act/Authority Registration No and Name of Act", fileName);
				continue;
			}

			String gstKey = normalizeKey(parsedRecord.getRowData().getOrDefault(CsvUploadValidator.GST_NUMBER, ""));
			if (!gstKey.isBlank() && !seenGstKeys.add(gstKey)) {
				validationFailures++;
				batchPersistenceService.saveFailedRecord(jobId, parsedRecord, null, "Duplicate GST number found", fileName);
				continue;
			}

			persistableBatch.add(parsedRecord);
		}

		CsvUploadBatchPersistenceResult result = batchPersistenceService.persistBatch(jobId, List.copyOf(persistableBatch),
				regUserId, fileName);
		CsvUploadJobEntity job = getRequiredJob(jobId);
		job.setSuccessRecords(job.getSuccessRecords() + result.successCount());
		job.setFailedRecords(job.getFailedRecords() + result.failedCount() + validationFailures);
		job.setProcessedRecords(job.getProcessedRecords() + batch.size());
		job.setLastProcessedValidRecord(job.getLastProcessedValidRecord() + batch.size());
		job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
		job.setProgressPercentage(calculateProgress(job.getProcessedRecords(), job.getTotalRecords()));
		job.setTotalSuccessPages(calculateTotalPages(job.getSuccessRecords(), job.getResultPageSize()));
		job.setTotalFailedPages(calculateTotalPages(job.getFailedRecords(), job.getResultPageSize()));
		job.setMessage("Processed " + job.getProcessedRecords() + " of " + job.getTotalRecords() + " records.");
		jobRepository.save(job);
	}

	private String buildDuplicateKey(java.util.Map<String, String> rowData, String primaryKey, String secondaryKey) {
		String primaryValue = normalizeTextKey(rowData.getOrDefault(primaryKey, ""));
		String secondaryValue = normalizeTextKey(rowData.getOrDefault(secondaryKey, ""));
		if (primaryValue.isBlank() || secondaryValue.isBlank()) {
			return "";
		}
		return primaryValue + "|" + secondaryValue;
	}

	private String normalizeTextKey(String value) {
		return value == null ? "" : value.trim().toUpperCase();
	}

	private String normalizeKey(String value) {
		return value == null ? "" : value.trim().toUpperCase().replace(" ", "");
	}

	private void handleStopRequest(String jobId) throws StopProcessingSignal {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (job.isCancelRequested()) {
			job.setStatus(CsvUploadLifecycleStatus.CANCELLED);
			job.setCancelRequested(false);
			job.setPauseRequested(false);
			job.setCompletedAt(LocalDateTime.now());
			job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
			job.setMessage("Processing cancelled.");
			jobRepository.save(job);
			throw new StopProcessingSignal();
		}

		if (job.isPauseRequested()) {
			job.setStatus(CsvUploadLifecycleStatus.PAUSED);
			job.setPauseRequested(false);
			job.setPausedAt(LocalDateTime.now());
			job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
			job.setMessage("Processing paused.");
			jobRepository.save(job);
			throw new StopProcessingSignal();
		}
	}

	private void markCompleted(String jobId) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		job.setStatus(CsvUploadLifecycleStatus.COMPLETED);
		job.setCompletedAt(LocalDateTime.now());
		job.setProgressPercentage(job.getTotalRecords() > 0 ? 100 : 0);
		job.setProcessedRecords(job.getTotalRecords());
		job.setPendingRecords(0);
		job.setMessage("Processing completed.");
		jobRepository.save(job);
	}

	private void markFailed(String jobId, Exception exception) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		job.setStatus(CsvUploadLifecycleStatus.FAILED);
		job.setPauseRequested(false);
		job.setCancelRequested(false);
		job.setCompletedAt(LocalDateTime.now());
		job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
		job.setMessage(exception.getMessage() == null || exception.getMessage().isBlank() ? "Processing failed."
				: exception.getMessage());
		jobRepository.save(job);
	}

	private CsvUploadJobEntity getRequiredJob(String jobId) {
		return jobRepository.findByJobId(jobId).orElseThrow(() -> new IllegalArgumentException("Upload job not found."));
	}

	private Integer resolveRegistryUserId(String username) {
		return userRepository.findByUsername(username).map(User::getRegistry).map(registry -> registry.getId().intValue())
				.orElse(0);
	}

	private int calculateProgress(int processed, int total) {
		if (total <= 0) {
			return 0;
		}
		return Math.min(100, (processed * 100) / total);
	}

	private int calculateTotalPages(int totalRecords, int pageSize) {
		if (pageSize <= 0 || totalRecords <= 0) {
			return 0;
		}
		return (int) Math.ceil((double) totalRecords / pageSize);
	}

	private static class StopProcessingSignal extends IOException {
		private static final long serialVersionUID = 1L;
	}
}
