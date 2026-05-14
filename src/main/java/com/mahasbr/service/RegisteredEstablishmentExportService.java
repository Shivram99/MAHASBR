package com.mahasbr.service;

import java.io.OutputStream;
import java.util.List;

public interface RegisteredEstablishmentExportService {

	void exportRegisteredEstablishmentsPdf(List<Long> districtIds, List<Long> talukaIds, String brn,
			OutputStream outputStream);
}
