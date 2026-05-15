package com.mahasbr.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mahasbr.dto.RegisteredEstablishmentExportDto;
import com.mahasbr.dto.RegisteredEstablishmentExportJobCreatedResponse;
import com.mahasbr.dto.RegisteredEstablishmentExportJobStatusResponse;
import com.mahasbr.entity.User;
import com.mahasbr.exception.ExportGenerationException;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.RegisteredEstablishmentExportQueryRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.service.RegisteredEstablishmentExportJobStore.ExportJobEntry;
import com.mahasbr.util.ExcelExportUtil;
import com.mahasbr.util.PdfExportUtil;

@Service
@Transactional(readOnly = true)
public class RegisteredEstablishmentExportServiceImpl implements RegisteredEstablishmentExportService {

	private static final Logger logger = LoggerFactory.getLogger(RegisteredEstablishmentExportServiceImpl.class);

	private static final int EXPORT_BATCH_SIZE = 2_000;
	private static final int PDF_TABLE_FLUSH_BATCH_SIZE = 4_000;
	private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	private final RegisteredEstablishmentExportQueryRepository exportQueryRepository;
	private final DistrictMasterRepository districtMasterRepository;
	private final TalukaMasterRepository talukaMasterRepository;
	private final UserRepository userRepository;
	private final PdfExportUtil pdfExportUtil;
	private final ExcelExportUtil excelExportUtil;
	private final RegisteredEstablishmentExportJobStore exportJobStore;

	public RegisteredEstablishmentExportServiceImpl(
			RegisteredEstablishmentExportQueryRepository exportQueryRepository,
			DistrictMasterRepository districtMasterRepository,
			TalukaMasterRepository talukaMasterRepository,
			UserRepository userRepository,
			PdfExportUtil pdfExportUtil,
			ExcelExportUtil excelExportUtil,
			RegisteredEstablishmentExportJobStore exportJobStore) {
		this.exportQueryRepository = exportQueryRepository;
		this.districtMasterRepository = districtMasterRepository;
		this.talukaMasterRepository = talukaMasterRepository;
		this.userRepository = userRepository;
		this.pdfExportUtil = pdfExportUtil;
		this.excelExportUtil = excelExportUtil;
		this.exportJobStore = exportJobStore;
	}

	@Override
	public void exportRegisteredEstablishmentsPdf(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream) {
		ExportRequestContext context = buildExportRequestContext(districtIds, talukaIds, brn);
		logger.info(
				"Starting registered establishments PDF export. scope={} registryId={} districtFilterCount={} talukaFilterCount={} brnPresent={}",
				context.scopeDisplayName(), context.registryId(), context.effectiveDistricts().size(),
				context.effectiveTalukas().size(), context.normalizedBrn() != null);

		try {
			writePdfExport(context, outputStream, ExportProgressListener.noop());
		} catch (DataAccessException ex) {
			logger.error(
					"Registered establishments PDF export failed because the database query did not complete successfully.",
					ex);
			throw new ExportGenerationException(
					"Registered establishments export is temporarily unavailable. Please try again.",
					HttpStatus.SERVICE_UNAVAILABLE,
					ex);
		} catch (Exception ex) {
			logger.error("Failed to export registered establishments PDF", ex);
			throw new ExportGenerationException("Failed to generate registered establishments PDF export.",
					HttpStatus.INTERNAL_SERVER_ERROR, ex);
		}
	}

	@Override
	public void exportRegisteredEstablishmentsExcel(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream) {
		ExportRequestContext context = buildExportRequestContext(districtIds, talukaIds, brn);
		logger.info(
				"Starting registered establishments Excel export. scope={} registryId={} districtFilterCount={} talukaFilterCount={} brnPresent={}",
				context.scopeDisplayName(), context.registryId(), context.effectiveDistricts().size(),
				context.effectiveTalukas().size(), context.normalizedBrn() != null);

		try {
			writeExcelExport(context, outputStream, ExportProgressListener.noop());
		} catch (DataAccessException ex) {
			logger.error(
					"Registered establishments Excel export failed because the database query did not complete successfully.",
					ex);
			throw new ExportGenerationException(
					"Registered establishments export is temporarily unavailable. Please try again.",
					HttpStatus.SERVICE_UNAVAILABLE,
					ex);
		} catch (Exception ex) {
			logger.error("Failed to export registered establishments Excel", ex);
			throw new ExportGenerationException("Failed to generate registered establishments Excel export.",
					HttpStatus.INTERNAL_SERVER_ERROR, ex);
		}
	}

	@Override
	public RegisteredEstablishmentExportJobCreatedResponse startExportJob(RegisteredEstablishmentExportFormat format,
			List<Long> districtIds, List<Long> talukaIds, String brn) {
		AuthenticatedUserContext currentUser = getAuthenticatedUserContext();
		buildExportRequestContext(districtIds, talukaIds, brn);

		String fileName = "registered-establishments-" + FILE_NAME_FORMATTER.format(LocalDateTime.now()) + "."
				+ format.getFileExtension();
		ExportJobEntry jobEntry = exportJobStore.create(currentUser.username(), format, fileName, districtIds, talukaIds,
				normalizeBrn(brn));

		logger.info("Queued registered establishments {} export job. jobId={} username={}", format.name(),
				jobEntry.jobId(), currentUser.username());
		return exportJobStore.toCreatedResponse(jobEntry);
	}

	@Override
	public RegisteredEstablishmentExportJobStatusResponse getExportJobStatus(String jobId) {
		AuthenticatedUserContext currentUser = getAuthenticatedUserContext();
		ExportJobEntry jobEntry = exportJobStore.getOwnedJob(jobId, currentUser.username());
		return exportJobStore.toStatusResponse(jobEntry);
	}

	@Override
	public RegisteredEstablishmentExportDownload getExportDownload(String jobId) {
		AuthenticatedUserContext currentUser = getAuthenticatedUserContext();
		ExportJobEntry jobEntry = exportJobStore.getOwnedJob(jobId, currentUser.username());

		if (jobEntry.status() != ExportJobStatus.COMPLETED || jobEntry.path() == null) {
			throw new ExportGenerationException("Export is not ready for download yet.", HttpStatus.CONFLICT);
		}

		if (!Files.exists(jobEntry.path())) {
			throw new ExportGenerationException("Generated export file is no longer available. Please request a new export.",
					HttpStatus.GONE);
		}

		return new RegisteredEstablishmentExportDownload(jobEntry.path(), jobEntry.fileName(),
				jobEntry.format().getMediaType(), jobEntry.fileSize());
	}

	@Override
	public void runExportJob(String jobId) {
		AuthenticatedUserContext currentUser = getAuthenticatedUserContext();
		ExportJobEntry jobEntry = exportJobStore.getOwnedJob(jobId, currentUser.username());
		Path tempFile = null;

		try {
			exportJobStore.markRunning(jobId, currentUser.username(), "Preparing data", "Validating export filters.");
			ExportRequestContext context = buildExportRequestContext(jobEntry.districtIds(), jobEntry.talukaIds(),
					jobEntry.brn());
			long totalRows = countExportRows(context);

			exportJobStore.updateProgress(jobId, currentUser.username(), "Preparing file", 5, 0, totalRows,
					"Preparing export file.");
			tempFile = exportJobStore.createTempFile(jobEntry.format(), jobId);

			try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
				ExportProgressListener progressListener = (processedRows) -> exportJobStore.updateProgress(jobId,
						currentUser.username(), "Generating file", calculateGenerationProgress(processedRows, totalRows),
						processedRows, totalRows, buildProgressMessage(processedRows, totalRows));

				switch (jobEntry.format()) {
				case PDF -> writePdfExport(context, outputStream, progressListener);
				case EXCEL -> writeExcelExport(context, outputStream, progressListener);
				}
			}

			long fileSize = Files.size(tempFile);
			exportJobStore.markCompleted(jobId, currentUser.username(), tempFile, fileSize);
			logger.info("Completed registered establishments {} export job. jobId={} rows={} fileSize={}",
					jobEntry.format().name(), jobId, totalRows, fileSize);
		} catch (DataAccessException ex) {
			deleteQuietly(tempFile);
			logger.error("Registered establishments export job failed due to database access. jobId={}", jobId, ex);
			exportJobStore.markFailed(jobId, currentUser.username(),
					"Export could not complete because the database did not respond in time. Please try again.");
		} catch (Exception ex) {
			deleteQuietly(tempFile);
			logger.error("Registered establishments export job failed. jobId={}", jobId, ex);
			exportJobStore.markFailed(jobId, currentUser.username(),
					"Export generation failed. Please retry the download request.");
		}
	}

	private void writePdfExport(ExportRequestContext context, OutputStream outputStream,
			ExportProgressListener progressListener) throws Exception {
		Document document = pdfExportUtil.createLandscapeDocument();
		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();
			document.addTitle("Registered Establishments Report");
			pdfExportUtil.addReportHeader(document, LocalDateTime.now(), context.filterSummary());

			PdfPTable table = pdfExportUtil.createRegisteredEstablishmentsTable();
			int[] pendingRows = { 0 };
			long exportedRows = appendAllRows(context, rows -> {
				pdfExportUtil.appendRows(table, rows);
				pendingRows[0] += rows.size();

				if (pendingRows[0] >= PDF_TABLE_FLUSH_BATCH_SIZE) {
					try {
						document.add(table);
					} catch (Exception exception) {
						throw new IllegalStateException("Failed to write PDF rows to the export document.", exception);
					}
					table.deleteBodyRows();
					pendingRows[0] = 0;
				}
			}, progressListener);

			if (exportedRows == 0) {
				pdfExportUtil.addNoDataRow(table);
			}

			document.add(table);
			logger.info("Completed registered establishments PDF export. exportedRows={}", exportedRows);
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	private void writeExcelExport(ExportRequestContext context, OutputStream outputStream,
			ExportProgressListener progressListener) throws IOException {
		long exportedRows = excelExportUtil.writeRegisteredEstablishmentsWorkbook(outputStream, LocalDateTime.now(),
				context.filterSummary(), consumer -> appendAllRows(context, rows -> {
					try {
						consumer.accept(rows);
					} catch (IOException exception) {
						throw new IllegalStateException("Failed while streaming Excel rows", exception);
					}
				}, progressListener));
		logger.info("Completed registered establishments Excel export. exportedRows={}", exportedRows);
	}

	private long appendAllRows(ExportRequestContext context, ExportRowBatchConsumer consumer,
			ExportProgressListener progressListener) {
		if (context.noResults()) {
			progressListener.onBatchProcessed(0);
			return 0;
		}

		long lastSeenSiNo = 0L;
		long exportedRows = 0L;

		while (true) {
			List<RegisteredEstablishmentExportDto> rows = exportQueryRepository.findBatchForExport(
					context.applyRegistryFilter(),
					context.registryId(),
					context.applyDistrictFilter(),
					context.effectiveDistricts(),
					context.applyTalukaFilter(),
					context.effectiveTalukas(),
					context.normalizedBrn(),
					lastSeenSiNo,
					EXPORT_BATCH_SIZE);

			if (rows.isEmpty()) {
				break;
			}

			consumer.accept(rows);
			exportedRows += rows.size();
			lastSeenSiNo = rows.get(rows.size() - 1).srNo();
			progressListener.onBatchProcessed(exportedRows);

			if (rows.size() < EXPORT_BATCH_SIZE) {
				break;
			}
		}

		return exportedRows;
	}

	private long countExportRows(ExportRequestContext context) {
		if (context.noResults()) {
			return 0L;
		}

		return exportQueryRepository.countForExport(
				context.applyRegistryFilter(),
				context.registryId(),
				context.applyDistrictFilter(),
				context.effectiveDistricts(),
				context.applyTalukaFilter(),
				context.effectiveTalukas(),
				context.normalizedBrn());
	}

	private int calculateGenerationProgress(long processedRows, long totalRows) {
		if (totalRows <= 0) {
			return 95;
		}

		long boundedProcessedRows = Math.min(processedRows, totalRows);
		return Math.min(95, 10 + (int) ((boundedProcessedRows * 85) / totalRows));
	}

	private String buildProgressMessage(long processedRows, long totalRows) {
		if (totalRows <= 0) {
			return "Generating export file.";
		}

		return "Processed " + processedRows + " of " + totalRows + " rows.";
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

	private ExportRequestContext buildExportRequestContext(List<Long> districtIds, List<Long> talukaIds, String brn) {
		List<Long> safeDistrictIds = distinctIds(districtIds);
		List<Long> safeTalukaIds = distinctIds(talukaIds);
		String normalizedBrn = normalizeBrn(brn);

		validateRequest(safeDistrictIds, safeTalukaIds, normalizedBrn);

		AuthenticatedUserContext currentUser = getAuthenticatedUserContext();
		ExportScopeType scopeType = resolveScope(currentUser.roles());
		List<String> allowedDistricts = resolveAllowedDistricts(currentUser, scopeType);
		List<String> requestedDistricts = resolveRequestedDistricts(safeDistrictIds);
		List<String> requestedTalukas = resolveRequestedTalukas(safeTalukaIds);
		List<String> effectiveDistricts = mergeEffectiveDistricts(allowedDistricts, requestedDistricts);

		boolean noResults = !requestedDistricts.isEmpty() && effectiveDistricts.isEmpty();
		Map<String, String> filterSummary = buildFilterSummary(scopeType, allowedDistricts, requestedDistricts,
				requestedTalukas, normalizedBrn);

		return new ExportRequestContext(
				scopeType.getDisplayName(),
				scopeType == ExportScopeType.REGISTRY,
				currentUser.registryId(),
				!effectiveDistricts.isEmpty(),
				effectiveDistricts,
				!requestedTalukas.isEmpty(),
				requestedTalukas,
				normalizedBrn,
				filterSummary,
				noResults);
	}

	private void validateRequest(List<Long> districtIds, List<Long> talukaIds, String normalizedBrn) {
		if (!talukaIds.isEmpty() && districtIds.isEmpty()) {
			throw new IllegalArgumentException("Taluka filter requires at least one district filter.");
		}

		if (normalizedBrn != null && !normalizedBrn.matches("\\d+")) {
			throw new IllegalArgumentException("BRN must contain digits only.");
		}
	}

	private AuthenticatedUserContext getAuthenticatedUserContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getPrincipal())) {
			throw new IllegalArgumentException("Authenticated user not found.");
		}

		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

		Set<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		Integer registryId = user.getRegistry() != null && user.getRegistry().getId() != null
				? Math.toIntExact(user.getRegistry().getId())
				: null;
		Long districtId = user.getDistrict() != null ? user.getDistrict().getDistrictId() : null;

		return new AuthenticatedUserContext(username, registryId, districtId, user.getDivisionCode(), roles);
	}

	private ExportScopeType resolveScope(Set<String> roles) {
		if (roles.contains("ROLE_REG_AUTH_API") || roles.contains("ROLE_REG_AUTH_CSV")) {
			return ExportScopeType.REGISTRY;
		}
		if (roles.contains("ROLE_DES_DISTRICT")) {
			return ExportScopeType.DISTRICT;
		}
		if (roles.contains("ROLE_DES_REGION")) {
			return ExportScopeType.REGION;
		}
		if (roles.contains("ROLE_DES_STATE")) {
			return ExportScopeType.STATE;
		}

		throw new IllegalArgumentException("User is not authorized to export registered establishments.");
	}

	private List<String> resolveAllowedDistricts(AuthenticatedUserContext currentUser, ExportScopeType scopeType) {
		return switch (scopeType) {
		case REGISTRY, STATE -> List.of();
		case DISTRICT -> {
			if (currentUser.districtId() == null) {
				throw new IllegalArgumentException("District is not configured for the authenticated user.");
			}
			yield districtMasterRepository.findDistrictNameById(currentUser.districtId())
					.map(List::of)
					.map(this::normalizeNames)
					.orElseThrow(() -> new IllegalArgumentException("District is not configured for the authenticated user."));
		}
		case REGION -> {
			if (!StringUtils.hasText(currentUser.divisionCode())) {
				throw new IllegalArgumentException("Division is not configured for the authenticated user.");
			}
			yield normalizeNames(districtMasterRepository.findDistrictNamesByDivisionCode(currentUser.divisionCode()));
		}
		};
	}

	private List<String> resolveRequestedDistricts(List<Long> districtIds) {
		if (districtIds.isEmpty()) {
			return List.of();
		}

		List<String> districts = normalizeNames(districtMasterRepository.findDistrictNamesByCensusDistrictCodes(districtIds));
		if (districts.size() != districtIds.size()) {
			throw new IllegalArgumentException("One or more district filters are invalid.");
		}
		return districts;
	}

	private List<String> resolveRequestedTalukas(List<Long> talukaIds) {
		if (talukaIds.isEmpty()) {
			return List.of();
		}

		List<String> talukaCodes = talukaIds.stream().map(code -> String.format("%05d", code)).toList();
		List<String> talukas = normalizeNames(talukaMasterRepository.findTalukaNameByCensusTalukaCode(talukaCodes));
		if (talukas.size() != talukaCodes.size()) {
			throw new IllegalArgumentException("One or more taluka filters are invalid.");
		}
		return talukas;
	}

	private List<String> mergeEffectiveDistricts(List<String> allowedDistricts, List<String> requestedDistricts) {
		if (allowedDistricts.isEmpty()) {
			return requestedDistricts;
		}
		if (requestedDistricts.isEmpty()) {
			return allowedDistricts;
		}

		Set<String> allowedSet = new LinkedHashSet<>(allowedDistricts);
		return requestedDistricts.stream().filter(allowedSet::contains).distinct().toList();
	}

	private Map<String, String> buildFilterSummary(ExportScopeType scopeType, List<String> allowedDistricts,
			List<String> requestedDistricts, List<String> requestedTalukas, String brn) {
		Map<String, String> filterSummary = new LinkedHashMap<>();
		filterSummary.put("Scope", scopeType.getDisplayName());
		filterSummary.put("District", formatDistrictSummary(scopeType, allowedDistricts, requestedDistricts));
		filterSummary.put("Taluka", requestedTalukas.isEmpty() ? "All" : String.join(", ", requestedTalukas));
		filterSummary.put("BRN", brn != null ? brn : "All");
		return filterSummary;
	}

	private String formatDistrictSummary(ExportScopeType scopeType, List<String> allowedDistricts,
			List<String> requestedDistricts) {
		if (!requestedDistricts.isEmpty()) {
			return String.join(", ", requestedDistricts);
		}
		if (!allowedDistricts.isEmpty() && scopeType != ExportScopeType.STATE) {
			return "All Accessible";
		}
		return "All";
	}

	private List<Long> distinctIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}

		return ids.stream().filter(Objects::nonNull).distinct().toList();
	}

	private String normalizeBrn(String brn) {
		if (!StringUtils.hasText(brn)) {
			return null;
		}

		return brn.trim();
	}

	private List<String> normalizeNames(List<String> values) {
		if (values == null || values.isEmpty()) {
			return List.of();
		}

		return values.stream()
				.filter(StringUtils::hasText)
				.map(String::trim)
				.map(String::toUpperCase)
				.collect(Collectors.toCollection(ArrayList::new))
				.stream()
				.distinct()
				.toList();
	}

	@FunctionalInterface
	private interface ExportRowBatchConsumer {
		void accept(List<RegisteredEstablishmentExportDto> rows);
	}

	@FunctionalInterface
	private interface ExportProgressListener {
		void onBatchProcessed(long processedRows);

		static ExportProgressListener noop() {
			return processedRows -> {
			};
		}
	}

	private record AuthenticatedUserContext(
			String username,
			Integer registryId,
			Long districtId,
			String divisionCode,
			Set<String> roles) {
	}

	private record ExportRequestContext(
			String scopeDisplayName,
			boolean applyRegistryFilter,
			Integer registryId,
			boolean applyDistrictFilter,
			List<String> effectiveDistricts,
			boolean applyTalukaFilter,
			List<String> effectiveTalukas,
			String normalizedBrn,
			Map<String, String> filterSummary,
			boolean noResults) {
	}

	private enum ExportScopeType {
		REGISTRY("Registry"),
		DISTRICT("District"),
		REGION("Region"),
		STATE("State");

		private final String displayName;

		ExportScopeType(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
