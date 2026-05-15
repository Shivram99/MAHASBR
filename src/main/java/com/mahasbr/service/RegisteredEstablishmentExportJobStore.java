package com.mahasbr.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.RegisteredEstablishmentExportJobCreatedResponse;
import com.mahasbr.dto.RegisteredEstablishmentExportJobStatusResponse;
import com.mahasbr.exception.ResourceNotFoundException;

@Component
public class RegisteredEstablishmentExportJobStore {

	private static final Duration RETENTION_WINDOW = Duration.ofHours(2);

	private final Map<String, ExportJobEntry> jobs = new ConcurrentHashMap<>();
	private final Path exportBaseDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "mahasbr-exports");

	public RegisteredEstablishmentExportJobStore() {
		try {
			Files.createDirectories(exportBaseDirectory);
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to initialize export temp directory.", ex);
		}
	}

	public ExportJobEntry create(String ownerUsername, RegisteredEstablishmentExportFormat format, String fileName,
			List<Long> districtIds, List<Long> talukaIds, String brn) {
		purgeExpiredJobs();

		String jobId = UUID.randomUUID().toString();
		Instant now = Instant.now();
		ExportJobEntry entry = new ExportJobEntry(
				jobId,
				ownerUsername,
				format,
				fileName,
				districtIds != null ? List.copyOf(districtIds) : List.of(),
				talukaIds != null ? List.copyOf(talukaIds) : List.of(),
				brn,
				ExportJobStatus.QUEUED,
				"Queued",
				0,
				null,
				null,
				"Export request accepted.",
				null,
				0L,
				now,
				now);

		jobs.put(jobId, entry);
		return entry;
	}

	public ExportJobEntry getOwnedJob(String jobId, String ownerUsername) {
		purgeExpiredJobs();

		ExportJobEntry entry = jobs.get(jobId);
		if (entry == null || !entry.ownerUsername().equals(ownerUsername)) {
			throw new ResourceNotFoundException("Export job not found.");
		}
		return entry;
	}

	public void markRunning(String jobId, String ownerUsername, String stage, String message) {
		update(jobId, ownerUsername, entry -> entry.withStatus(ExportJobStatus.RUNNING, stage, 2, message));
	}

	public void updateProgress(String jobId, String ownerUsername, String stage, int progressPercent,
			long processedRows, long totalRows, String message) {
		update(jobId, ownerUsername,
				entry -> entry.withProgress(stage, progressPercent, processedRows, totalRows, message));
	}

	public void markCompleted(String jobId, String ownerUsername, Path path, long fileSize) {
		update(jobId, ownerUsername, entry -> entry.withCompletion(path, fileSize));
	}

	public void markFailed(String jobId, String ownerUsername, String message) {
		update(jobId, ownerUsername, entry -> entry.withFailure(message));
	}

	public RegisteredEstablishmentExportJobCreatedResponse toCreatedResponse(ExportJobEntry entry) {
		return new RegisteredEstablishmentExportJobCreatedResponse(
				entry.jobId(),
				entry.status().name(),
				entry.stage(),
				entry.progressPercent(),
				entry.format().name(),
				entry.fileName());
	}

	public RegisteredEstablishmentExportJobStatusResponse toStatusResponse(ExportJobEntry entry) {
		return new RegisteredEstablishmentExportJobStatusResponse(
				entry.jobId(),
				entry.status().name(),
				entry.stage(),
				entry.progressPercent(),
				entry.format().name(),
				entry.fileName(),
				entry.totalRows(),
				entry.processedRows(),
				entry.message(),
				entry.status() == ExportJobStatus.COMPLETED && entry.path() != null);
	}

	public Path createTempFile(RegisteredEstablishmentExportFormat format, String jobId) throws IOException {
		return Files.createTempFile(exportBaseDirectory, "registered-establishments-" + jobId + "-", "."
				+ format.getFileExtension());
	}

	private void update(String jobId, String ownerUsername, JobMutator mutator) {
		jobs.compute(jobId, (key, existing) -> {
			if (existing == null || !existing.ownerUsername().equals(ownerUsername)) {
				throw new ResourceNotFoundException("Export job not found.");
			}
			return mutator.apply(existing);
		});
	}

	private void purgeExpiredJobs() {
		Instant threshold = Instant.now().minus(RETENTION_WINDOW);
		jobs.entrySet().removeIf(entry -> {
			ExportJobEntry job = entry.getValue();
			if (job.updatedAt().isBefore(threshold)) {
				deleteQuietly(job.path());
				return true;
			}
			return false;
		});
	}

	private void deleteQuietly(Path path) {
		if (path == null) {
			return;
		}

		try {
			Files.deleteIfExists(path);
		} catch (IOException ignored) {
		}
	}

	@FunctionalInterface
	private interface JobMutator {
		ExportJobEntry apply(ExportJobEntry entry);
	}

	public record ExportJobEntry(
			String jobId,
			String ownerUsername,
			RegisteredEstablishmentExportFormat format,
			String fileName,
			List<Long> districtIds,
			List<Long> talukaIds,
			String brn,
			ExportJobStatus status,
			String stage,
			int progressPercent,
			Long totalRows,
			Long processedRows,
			String message,
			Path path,
			long fileSize,
			Instant createdAt,
			Instant updatedAt) {

		ExportJobEntry withStatus(ExportJobStatus nextStatus, String nextStage, int nextProgress, String nextMessage) {
			return new ExportJobEntry(
					jobId,
					ownerUsername,
					format,
					fileName,
					districtIds,
					talukaIds,
					brn,
					nextStatus,
					nextStage,
					nextProgress,
					totalRows,
					processedRows,
					nextMessage,
					path,
					fileSize,
					createdAt,
					Instant.now());
		}

		ExportJobEntry withProgress(String nextStage, int nextProgress, long nextProcessedRows, long nextTotalRows,
				String nextMessage) {
			return new ExportJobEntry(
					jobId,
					ownerUsername,
					format,
					fileName,
					districtIds,
					talukaIds,
					brn,
					ExportJobStatus.RUNNING,
					nextStage,
					nextProgress,
					nextTotalRows,
					nextProcessedRows,
					nextMessage,
					path,
					fileSize,
					createdAt,
					Instant.now());
		}

		ExportJobEntry withCompletion(Path nextPath, long nextFileSize) {
			return new ExportJobEntry(
					jobId,
					ownerUsername,
					format,
					fileName,
					districtIds,
					talukaIds,
					brn,
					ExportJobStatus.COMPLETED,
					"Ready",
					100,
					totalRows,
					processedRows,
					"Export is ready for download.",
					nextPath,
					nextFileSize,
					createdAt,
					Instant.now());
		}

		ExportJobEntry withFailure(String nextMessage) {
			return new ExportJobEntry(
					jobId,
					ownerUsername,
					format,
					fileName,
					districtIds,
					talukaIds,
					brn,
					ExportJobStatus.FAILED,
					"Failed",
					progressPercent,
					totalRows,
					processedRows,
					nextMessage,
					path,
					fileSize,
					createdAt,
					Instant.now());
		}
	}
}
