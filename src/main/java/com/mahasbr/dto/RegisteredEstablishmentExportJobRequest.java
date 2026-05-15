package com.mahasbr.dto;

import java.util.List;

public record RegisteredEstablishmentExportJobRequest(
		String format,
		List<Long> districtIds,
		List<Long> talukaIds,
		String brn) {
}
