package com.mahasbr.dto;

public record RegisteredEstablishmentExportDto(
		Long srNo,
		String brn,
		String establishmentName,
		String city,
		String district,
		String institutionType) {
}
