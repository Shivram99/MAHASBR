package com.mahasbr.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.CsvUploadFailedPageResponse;
import com.mahasbr.dto.CsvUploadFailedRecordDto;
import com.mahasbr.dto.CsvUploadInitResponse;
import com.mahasbr.dto.CsvUploadParsedRecordDto;
import com.mahasbr.dto.CsvUploadPreviewResponse;
import com.mahasbr.dto.CsvUploadRecordDto;
import com.mahasbr.dto.CsvUploadStatusResponse;
import com.mahasbr.dto.CsvUploadSuccessPageResponse;
import com.mahasbr.dto.CsvUploadSuccessRecordDto;
import com.mahasbr.entity.CsvUploadFailedRecordEntity;
import com.mahasbr.entity.CsvUploadJobEntity;
import com.mahasbr.entity.CsvUploadSuccessRecordEntity;
import com.mahasbr.parser.FileParserService;
import com.mahasbr.repository.CsvUploadFailedRecordRepository;
import com.mahasbr.repository.CsvUploadJobRepository;
import com.mahasbr.repository.CsvUploadSuccessRecordRepository;
import com.mahasbr.validator.CsvUploadValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvUploadServiceImpl implements CsvUploadService {

	private static final int MAX_FILE_SIZE_MB = 50;
	private static final int DEFAULT_PAGE_SIZE = 100;
	private static final int MAX_PAGE_SIZE = 500;
	private static final FileUploadPolicy FILE_UPLOAD_POLICY = new FileUploadPolicy(MAX_FILE_SIZE_MB,
			Set.of("csv", "xls", "xlsx"),
			Set.of("text/csv", "text/plain", "application/csv", "application/vnd.ms-excel",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/octet-stream"));

	private final List<FileParserService> parsers;
	private final CsvUploadValidator validator;
	private final CsvUploadStorageService storageService;
	private final CsvUploadAsyncProcessor asyncProcessor;
	private final CsvUploadJobRepository jobRepository;
	private final CsvUploadFailedRecordRepository failedRecordRepository;
	private final CsvUploadSuccessRecordRepository successRecordRepository;
	private final ObjectMapper objectMapper;

	@Override
	public CsvUploadInitResponse upload(MultipartFile file, String username) {
		String jobId = UUID.randomUUID().toString();
		String storedFileName = storageService.storeUploadedFile(jobId, file, FILE_UPLOAD_POLICY);
		CsvUploadJobEntity job = CsvUploadJobEntity.builder().jobId(jobId)
				.fileName(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename()).storedFileName(storedFileName)
				.fileSize(file.getSize()).status(CsvUploadLifecycleStatus.UPLOADED)
				.message("File uploaded successfully. Preview the file to continue.").createdBy(username)
				.previewPageSize(DEFAULT_PAGE_SIZE).resultPageSize(DEFAULT_PAGE_SIZE).build();
		jobRepository.save(job);
		log.info("CSV upload job created. jobId={}, fileName={}, createdBy={}", jobId, job.getFileName(), username);
		return CsvUploadInitResponse.builder().jobId(job.getJobId()).fileName(job.getFileName()).fileSize(job.getFileSize())
				.status(job.getStatus().name()).message(job.getMessage()).build();
	}

	@Override
	public CsvUploadPreviewResponse generatePreview(String jobId, int page, int size) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		validatePreviewRegeneration(job);
		int effectivePageSize = normalizePageSize(size);
		resetForPreview(job, effectivePageSize);

		Set<String> seenActKeys = new HashSet<>();
		Set<String> seenGstKeys = new HashSet<>();
		CsvUploadValidator.CsvUploadValidationContext validationContext = validator.prepareValidationContext();

		try (CsvUploadPageWriter<CsvUploadRecordDto> previewWriter = new CsvUploadPageWriter<>(objectMapper,
				storageService.previewPagesDirectory(jobId), effectivePageSize, "page");
				var validRowsWriter = storageService.newValidRowsWriter(jobId);
				var invalidRowsWriter = storageService.newInvalidRowsWriter(jobId);
				var failedPreviewWriter = java.nio.file.Files.newBufferedWriter(storageService.failedPreviewRowsPath(jobId),
						java.nio.charset.StandardCharsets.UTF_8)) {

			parseStoredFile(job, (rowNumber, rowData) -> {
				var validation = validator.validateForPreview(rowData, seenActKeys, seenGstKeys, validationContext);
				CsvUploadRecordDto previewRecord = CsvUploadRecordDto.builder().rowNumber(rowNumber)
						.establishmentName(value(rowData, CsvUploadValidator.ESTABLISHMENT_NAME))
						.district(value(rowData, CsvUploadValidator.DISTRICT)).taluka(value(rowData, CsvUploadValidator.TALUKA))
						.mobileNo(value(rowData, CsvUploadValidator.MOBILE_NO)).email(value(rowData, CsvUploadValidator.EMAIL))
						.pan(value(rowData, CsvUploadValidator.PAN)).gstNumber(value(rowData, CsvUploadValidator.GST_NUMBER))
						.nicCode(value(rowData, CsvUploadValidator.NIC_CODE)).valid(validation.isValid())
						.errorMessage(validation.getErrorMessage()).build();
				previewWriter.append(previewRecord);

				job.setTotalRecords(job.getTotalRecords() + 1);
				if (validation.isValid()) {
					job.setValidRecords(job.getValidRecords() + 1);
					storageService.appendJsonLine(validRowsWriter,
							CsvUploadParsedRecordDto.builder().rowNumber(rowNumber).rowData(rowData).build());
				} else {
					job.setInvalidRecords(job.getInvalidRecords() + 1);
					if (validation.isDuplicate()) {
						job.setDuplicateRecords(job.getDuplicateRecords() + 1);
					}
					CsvUploadParsedRecordDto parsedRecord = CsvUploadParsedRecordDto.builder().rowNumber(rowNumber).rowData(rowData)
							.build();
					storageService.appendJsonLine(invalidRowsWriter, parsedRecord);
					storageService.appendJsonLine(failedPreviewWriter, CsvUploadFailedRecordDto.builder().rowNumber(rowNumber)
							.establishmentName(value(rowData, CsvUploadValidator.ESTABLISHMENT_NAME))
							.errorReason(validation.getErrorMessage()).rawData(rawData(parsedRecord)).build());
				}
			});

			previewWriter.close();
			job.setTotalPreviewPages(previewWriter.getPageCounter());
			job.setFailedRecords(job.getInvalidRecords());
			job.setProcessedRecords(job.getInvalidRecords());
			job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
			job.setProgressPercentage(calculateProgress(job.getProcessedRecords(), job.getTotalRecords()));
			job.setPreviewReady(true);
			job.setMessage("Preview generated successfully. Start processing to import valid rows.");
			job.setTotalFailedPages(calculateTotalPages(job.getFailedRecords(), effectivePageSize));
			jobRepository.save(job);
			return getPreviewPage(jobId, page, effectivePageSize);
		} catch (Exception exception) {
			job.setStatus(CsvUploadLifecycleStatus.FAILED);
			job.setPreviewReady(false);
			job.setMessage(resolveMessage(exception, "Preview generation failed."));
			jobRepository.save(job);
			throw new IllegalStateException(job.getMessage(), exception);
		}
	}

	@Override
	public CsvUploadPreviewResponse getPreviewPage(String jobId, int page, int size) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (!job.isPreviewReady()) {
			throw new IllegalArgumentException("Preview is not ready for this upload.");
		}

		int effectivePageSize = job.getPreviewPageSize() > 0 ? job.getPreviewPageSize() : normalizePageSize(size);
		int effectivePage = Math.max(page, 0);
		List<CsvUploadRecordDto> records = storageService.readPage(storageService.previewPagesDirectory(jobId), effectivePage,
				new TypeReference<List<CsvUploadRecordDto>>() {
				});
		return CsvUploadPreviewResponse.builder().jobId(job.getJobId()).fileName(job.getFileName()).fileSize(job.getFileSize())
				.status(job.getStatus().name()).message(job.getMessage()).totalRecords(job.getTotalRecords())
				.validRecords(job.getValidRecords()).invalidRecords(job.getInvalidRecords())
				.duplicateRecords(job.getDuplicateRecords()).pageNumber(effectivePage).pageSize(effectivePageSize)
				.totalPages(job.getTotalPreviewPages()).previewReady(job.isPreviewReady()).records(records).build();
	}

	@Override
	public CsvUploadStatusResponse startProcessing(String jobId, String username) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (!job.isPreviewReady()) {
			throw new IllegalArgumentException("Generate preview before processing records.");
		}
		if (job.getStatus() == CsvUploadLifecycleStatus.PROCESSING) {
			return buildStatusResponse(job);
		}
		if (job.getStatus() != CsvUploadLifecycleStatus.UPLOADED) {
			throw new IllegalArgumentException("This job cannot be started in its current state.");
		}
		if (job.getValidRecords() <= 0) {
			throw new IllegalArgumentException("No valid records are available to process.");
		}

		job.setStatus(CsvUploadLifecycleStatus.PROCESSING);
		job.setStartedAt(job.getStartedAt() == null ? java.time.LocalDateTime.now() : job.getStartedAt());
		job.setPauseRequested(false);
		job.setCancelRequested(false);
		job.setCompletedAt(null);
		job.setPausedAt(null);
		job.setMessage("Processing started.");
		jobRepository.save(job);
		asyncProcessor.process(jobId, username);
		return buildStatusResponse(job);
	}

	@Override
	public CsvUploadStatusResponse pauseProcessing(String jobId) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (job.getStatus() != CsvUploadLifecycleStatus.PROCESSING) {
			throw new IllegalArgumentException("Only processing jobs can be paused.");
		}
		job.setPauseRequested(true);
		job.setMessage("Pause requested. Waiting for the current batch to finish.");
		jobRepository.save(job);
		return buildStatusResponse(job);
	}

	@Override
	public CsvUploadStatusResponse resumeProcessing(String jobId, String username) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (job.getStatus() != CsvUploadLifecycleStatus.PAUSED) {
			throw new IllegalArgumentException("Only paused jobs can be resumed.");
		}
		if (job.getPendingRecords() <= 0) {
			throw new IllegalArgumentException("There are no pending records left to process.");
		}

		job.setStatus(CsvUploadLifecycleStatus.PROCESSING);
		job.setPauseRequested(false);
		job.setCancelRequested(false);
		job.setResumedAt(java.time.LocalDateTime.now());
		job.setPausedAt(null);
		job.setMessage("Processing resumed.");
		jobRepository.save(job);
		asyncProcessor.process(jobId, username);
		return buildStatusResponse(job);
	}

	@Override
	public CsvUploadStatusResponse cancelProcessing(String jobId) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		if (job.getStatus() == CsvUploadLifecycleStatus.CANCELLED || job.getStatus() == CsvUploadLifecycleStatus.COMPLETED) {
			return buildStatusResponse(job);
		}

		if (job.getStatus() == CsvUploadLifecycleStatus.PAUSED || job.getStatus() == CsvUploadLifecycleStatus.UPLOADED
				|| job.getStatus() == CsvUploadLifecycleStatus.FAILED) {
			job.setStatus(CsvUploadLifecycleStatus.CANCELLED);
			job.setCancelRequested(false);
			job.setPauseRequested(false);
			job.setCompletedAt(java.time.LocalDateTime.now());
			job.setPendingRecords(Math.max(0, job.getTotalRecords() - job.getProcessedRecords()));
			job.setMessage("Processing cancelled.");
			jobRepository.save(job);
			return buildStatusResponse(job);
		}

		job.setCancelRequested(true);
		job.setPauseRequested(false);
		job.setMessage("Cancellation requested. Waiting for the current batch to finish.");
		jobRepository.save(job);
		return buildStatusResponse(job);
	}

	@Override
	public CsvUploadStatusResponse getStatus(String jobId) {
		return buildStatusResponse(getRequiredJob(jobId));
	}

	@Override
	public CsvUploadSuccessPageResponse getSuccessPage(String jobId, int page, int size) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		int effectivePageSize = job.getResultPageSize() > 0 ? job.getResultPageSize() : normalizePageSize(size);
		int effectivePage = Math.max(page, 0);
		Page<CsvUploadSuccessRecordEntity> successPage = successRecordRepository
				.findByJobIdOrderByRowNumberAsc(jobId, PageRequest.of(effectivePage, effectivePageSize));
		List<CsvUploadSuccessRecordDto> records = successPage.getContent().stream()
				.map(record -> CsvUploadSuccessRecordDto.builder().rowNumber(record.getRowNumber()).brn(record.getBrn())
						.establishmentName(record.getEstablishmentName()).rawData(record.getRawData()).build())
				.toList();
		return CsvUploadSuccessPageResponse.builder().jobId(jobId).pageNumber(effectivePage).pageSize(effectivePageSize)
				.totalPages(successPage.getTotalPages()).totalRecords((int) successPage.getTotalElements()).records(records).build();
	}

	@Override
	public CsvUploadFailedPageResponse getFailedPage(String jobId, int page, int size) {
		CsvUploadJobEntity job = getRequiredJob(jobId);
		int effectivePageSize = job.getResultPageSize() > 0 ? job.getResultPageSize() : normalizePageSize(size);
		int effectivePage = Math.max(page, 0);
		if (shouldServePreviewFailedRecords(jobId, job)) {
			CsvUploadStorageService.JsonLinesPage<CsvUploadFailedRecordDto> failedPreviewPage = storageService
					.readJsonLinesPage(storageService.failedPreviewRowsPath(jobId), effectivePage, effectivePageSize,
							CsvUploadFailedRecordDto.class);
			return CsvUploadFailedPageResponse.builder().jobId(jobId).pageNumber(effectivePage).pageSize(effectivePageSize)
					.totalPages(calculateTotalPages(failedPreviewPage.totalRecords(), effectivePageSize))
					.totalRecords(failedPreviewPage.totalRecords()).records(failedPreviewPage.records()).build();
		}

		Page<CsvUploadFailedRecordEntity> failedPage = failedRecordRepository
				.findByJobIdOrderByRowNumberAsc(jobId, PageRequest.of(effectivePage, effectivePageSize));
		List<CsvUploadFailedRecordDto> records = failedPage.getContent().stream()
				.map(record -> CsvUploadFailedRecordDto.builder().rowNumber(record.getRowNumber())
						.establishmentName(record.getEstablishmentName()).brn(record.getBrn())
						.errorReason(record.getErrorMessage()).rawData(record.getRawData()).build())
				.toList();
		return CsvUploadFailedPageResponse.builder().jobId(jobId).pageNumber(effectivePage).pageSize(effectivePageSize)
				.totalPages(failedPage.getTotalPages()).totalRecords((int) failedPage.getTotalElements()).records(records).build();
	}

	private void parseStoredFile(CsvUploadJobEntity job, com.mahasbr.parser.CsvUploadRowHandler rowHandler) throws IOException {
		FileParserService parser = parsers.stream().filter(candidate -> candidate.supports(job.getFileName())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported file type."));
		parser.parse(storageService.sourceFilePath(job), rowHandler);
	}

	private CsvUploadJobEntity getRequiredJob(String jobId) {
		return jobRepository.findByJobId(jobId).orElseThrow(() -> new IllegalArgumentException("Upload job not found."));
	}

	private void validatePreviewRegeneration(CsvUploadJobEntity job) {
		if (job.getStatus() == CsvUploadLifecycleStatus.PROCESSING || job.getStatus() == CsvUploadLifecycleStatus.PAUSED
				|| job.getStatus() == CsvUploadLifecycleStatus.COMPLETED) {
			throw new IllegalArgumentException("Preview cannot be regenerated for a job that has already started processing.");
		}
	}

	private boolean shouldServePreviewFailedRecords(String jobId, CsvUploadJobEntity job) {
		return job.isPreviewReady() && job.getStatus() == CsvUploadLifecycleStatus.UPLOADED
				&& failedRecordRepository.countByJobId(jobId) == 0;
	}

	private void resetForPreview(CsvUploadJobEntity job, int pageSize) {
		storageService.resetPreviewArtifacts(job.getJobId());
		failedRecordRepository.deleteByJobId(job.getJobId());
		successRecordRepository.deleteByJobId(job.getJobId());
		job.setStatus(CsvUploadLifecycleStatus.UPLOADED);
		job.setMessage("Generating preview.");
		job.setPreviewReady(false);
		job.setPauseRequested(false);
		job.setCancelRequested(false);
		job.setPreviewPageSize(pageSize);
		job.setResultPageSize(pageSize);
		job.setTotalRecords(0);
		job.setProcessedRecords(0);
		job.setSuccessRecords(0);
		job.setFailedRecords(0);
		job.setPendingRecords(0);
		job.setProgressPercentage(0);
		job.setValidRecords(0);
		job.setInvalidRecords(0);
		job.setDuplicateRecords(0);
		job.setLastProcessedValidRecord(0);
		job.setTotalPreviewPages(0);
		job.setTotalSuccessPages(0);
		job.setTotalFailedPages(0);
		job.setStartedAt(null);
		job.setCompletedAt(null);
		job.setPausedAt(null);
		job.setResumedAt(null);
		jobRepository.save(job);
	}

	private CsvUploadStatusResponse buildStatusResponse(CsvUploadJobEntity job) {
		return CsvUploadStatusResponse.builder().jobId(job.getJobId()).fileName(job.getFileName()).fileSize(job.getFileSize())
				.status(job.getStatus().name()).message(job.getMessage()).progressPercentage(job.getProgressPercentage())
				.totalRecords(job.getTotalRecords()).processedRecords(job.getProcessedRecords())
				.successRecords(job.getSuccessRecords()).failedRecords(job.getFailedRecords())
				.pendingRecords(job.getPendingRecords()).validRecords(job.getValidRecords())
				.invalidRecords(job.getInvalidRecords()).duplicateRecords(job.getDuplicateRecords())
				.totalPreviewPages(job.getTotalPreviewPages()).totalSuccessPages(job.getTotalSuccessPages())
				.totalFailedPages(job.getTotalFailedPages()).previewReady(job.isPreviewReady())
				.canStart(job.isPreviewReady() && job.getValidRecords() > 0 && job.getStatus() == CsvUploadLifecycleStatus.UPLOADED)
				.canPause(job.getStatus() == CsvUploadLifecycleStatus.PROCESSING && !job.isPauseRequested()
						&& !job.isCancelRequested())
				.canResume(job.getStatus() == CsvUploadLifecycleStatus.PAUSED && job.getPendingRecords() > 0)
				.canCancel(job.getStatus() == CsvUploadLifecycleStatus.UPLOADED
						|| job.getStatus() == CsvUploadLifecycleStatus.PROCESSING
						|| job.getStatus() == CsvUploadLifecycleStatus.PAUSED)
				.build();
	}

	private int normalizePageSize(int requestedSize) {
		if (requestedSize <= 0) {
			return DEFAULT_PAGE_SIZE;
		}
		return Math.min(requestedSize, MAX_PAGE_SIZE);
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

	private String resolveMessage(Exception exception, String fallback) {
		return exception.getMessage() == null || exception.getMessage().isBlank() ? fallback : exception.getMessage();
	}

	private String rawData(CsvUploadParsedRecordDto parsedRecord) {
		try {
			return objectMapper.writeValueAsString(parsedRecord.getRowData());
		} catch (JsonProcessingException exception) {
			return parsedRecord.getRowData().toString();
		}
	}

	private String value(java.util.Map<String, String> rowData, String key) {
		return rowData.getOrDefault(key, "").trim();
	}
}
