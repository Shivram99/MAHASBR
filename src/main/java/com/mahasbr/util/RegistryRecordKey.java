package com.mahasbr.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Locale;

import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

/**
 * Builds a stable key that identifies one establishment from one registry, so a repeated
 * sync can find the row it created before.
 *
 * <ul>
 * <li>If the source sends its own registration number(s), that is the identity.</li>
 * <li>Otherwise a fingerprint of name + PAN + GST + location + registration date is used.</li>
 * </ul>
 * The value is hashed (SHA-256, 64 hex chars) so it always fits the column.
 */
public final class RegistryRecordKey {

	private RegistryRecordKey() {
	}

	public static String of(MstRegistryDetailsPagesDTO dto) {
		return compute(dto.getActAuthorityRegistrationNumbers(), dto.getNameOfEstablishmentOrOwner(),
				dto.getPanNumber(), dto.getGstNumber(), dto.getDistrict(), dto.getTaluka(), dto.getTownVillage(),
				dto.getDateOfRegistration());
	}

	public static String of(MstRegistryDetailsPageEntity entity) {
		return compute(entity.getActAuthorityRegistrationNumbers(), entity.getNameOfEstablishmentOrOwner(),
				entity.getPanNumber(), entity.getGstNumber(), entity.getDistrict(), entity.getTaluka(),
				entity.getTownVillage(), entity.getDateOfRegistration());
	}

	static String compute(String registrationNumbers, String name, String pan, String gst, String district,
			String taluka, String village, LocalDate dateOfRegistration) {
		String registration = normalize(registrationNumbers);
		String canonical = !registration.isEmpty()
				? "R|" + registration
				: String.join("|", "F", normalize(name), normalize(pan), normalize(gst), normalize(district),
						normalize(taluka), normalize(village),
						dateOfRegistration != null ? dateOfRegistration.toString() : "");
		return sha256(canonical);
	}

	/** Upper-case, trimmed, inner whitespace collapsed: "  abc   Ltd " and "ABC LTD" match. */
	private static String normalize(String value) {
		return value == null ? "" : value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
	}

	private static String sha256(String value) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}
}
