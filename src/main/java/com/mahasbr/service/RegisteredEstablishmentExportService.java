package com.mahasbr.service;

import java.io.OutputStream;
import java.util.List;

import com.mahasbr.dto.RegisteredEstablishmentExportJobCreatedResponse;
import com.mahasbr.dto.RegisteredEstablishmentExportJobStatusResponse;

public interface RegisteredEstablishmentExportService {

	void exportRegisteredEstablishmentsPdf(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream);

	void exportRegisteredEstablishmentsExcel(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream);

	RegisteredEstablishmentExportJobCreatedResponse startExportJob(RegisteredEstablishmentExportFormat format,
			List<Long> districtIds, List<Long> talukaIds, String brn);

	RegisteredEstablishmentExportJobStatusResponse getExportJobStatus(String jobId);

	RegisteredEstablishmentExportDownload getExportDownload(String jobId);

	void runExportJob(String jobId);
}
