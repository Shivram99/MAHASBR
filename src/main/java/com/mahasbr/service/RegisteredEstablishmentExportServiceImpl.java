package com.mahasbr.service;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.mahasbr.dto.RegisteredEstablishmentExportDto;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.RegisteredEstablishmentExportRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.util.PdfExportUtil;

@Service
public class RegisteredEstablishmentExportServiceImpl implements RegisteredEstablishmentExportService {

	private static final int EXPORT_BATCH_SIZE = 1000;

	private final RegisteredEstablishmentExportRepository registeredEstablishmentExportRepository;
	private final DistrictMasterRepository districtMasterRepository;
	private final TalukaMasterRepository talukaMasterRepository;
	private final PdfExportUtil pdfExportUtil;

	public RegisteredEstablishmentExportServiceImpl(
			RegisteredEstablishmentExportRepository registeredEstablishmentExportRepository,
			DistrictMasterRepository districtMasterRepository,
			TalukaMasterRepository talukaMasterRepository,
			PdfExportUtil pdfExportUtil) {
		this.registeredEstablishmentExportRepository = registeredEstablishmentExportRepository;
		this.districtMasterRepository = districtMasterRepository;
		this.talukaMasterRepository = talukaMasterRepository;
		this.pdfExportUtil = pdfExportUtil;
	}

	@Override
	public void exportRegisteredEstablishmentsPdf(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream) {
		Integer registryId = getAuthenticatedRegistryId();
		List<Long> safeDistrictIds = districtIds == null ? List.of() : districtIds;
		List<Long> safeTalukaIds = talukaIds == null ? List.of() : talukaIds;

		List<String> districtNames = districtMasterRepository.findDistrictNamesByCensusDistrictCodes(safeDistrictIds);
		List<String> talukaNames = talukaMasterRepository.findTalukaNameByCensusTalukaCode(
				safeTalukaIds.stream().map(code -> String.format("%05d", code)).collect(Collectors.toList()));

		List<String> districtNamesLower = districtNames.stream().map(String::toLowerCase).toList();
		List<String> talukaNamesLower = talukaNames.stream().map(String::toLowerCase).toList();
		String normalizedBrn = StringUtils.hasText(brn) ? brn.trim() : null;

		Document document = pdfExportUtil.createLandscapeDocument();
		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();
			document.addTitle("Registered Establishments Report");

			Map<String, String> filterSummary = new LinkedHashMap<>();
			filterSummary.put("Registry ID", registryId.toString());
			filterSummary.put("District", districtNames.isEmpty() ? "All" : String.join(", ", districtNames));
			filterSummary.put("Taluka", talukaNames.isEmpty() ? "All" : String.join(", ", talukaNames));
			filterSummary.put("BRN", normalizedBrn != null ? normalizedBrn : "All");

			pdfExportUtil.addReportHeader(document, LocalDateTime.now(), filterSummary);

			PdfPTable table = pdfExportUtil.createRegisteredEstablishmentsTable();
			Pageable pageable = PageRequest.of(0, EXPORT_BATCH_SIZE);
			boolean wroteAnyRows = false;

			while (true) {
				Slice<RegisteredEstablishmentExportDto> slice = registeredEstablishmentExportRepository.findForPdfExport(
						true,
						registryId,
						!districtNamesLower.isEmpty(),
						districtNamesLower,
						!talukaNamesLower.isEmpty(),
						talukaNamesLower,
						normalizedBrn,
						pageable);

				if (slice.hasContent()) {
					pdfExportUtil.appendRows(table, slice.getContent());
					wroteAnyRows = true;
				}

				if (!slice.hasNext()) {
					break;
				}
				pageable = slice.nextPageable();
			}

			if (!wroteAnyRows) {
				pdfExportUtil.addNoDataRow(table);
			}

			document.add(table);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to export registered establishments PDF", ex);
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	private Integer getAuthenticatedRegistryId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
			throw new IllegalArgumentException("Authenticated registry user not found");
		}
		if (userDetails.getRegistryId() == null) {
			throw new IllegalArgumentException("Registry ID is not available for the authenticated user");
		}
		return Math.toIntExact(userDetails.getRegistryId());
	}
}
