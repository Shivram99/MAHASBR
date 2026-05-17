package com.mahasbr.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.CsvUploadValidationResultDto;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.NICCodeRepository;
import com.mahasbr.repository.TalukaMasterRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CsvUploadValidator {

	public static final String ESTABLISHMENT_NAME = "NAME_OF_ESTABLISHMENT/OWNER";
	public static final String HOUSE_NO = "HOUSE_NO";
	public static final String STREET_NAME = "STREET_NAME";
	public static final String LOCALITY = "LOCALITY";
	public static final String TOWN_VILLAGE = "TOWN_VILLAGE";
	public static final String TALUKA = "TALUKA";
	public static final String DISTRICT = "DISTRICT";
	public static final String PIN_CODE = "PIN_CODE";
	public static final String SECTOR = "SECTOR(RURAL/URBAN)";
	public static final String ACT_REGISTRATION_NO = "ACT/AUTHORITY_REGISTRATION_NO";
	public static final String NAME_OF_ACT = "NAME_OF_ACT";
	public static final String NAME_OF_AUTHORITY = "NAME_OF_AUTHORITY";
	public static final String MOBILE_NO = "TEL/MOB_NO";
	public static final String EMAIL = "EMAIL";
	public static final String PAN = "PAN";
	public static final String TAN = "TAN";
	public static final String WARD_NUMBER = "WARD_NUMBER";
	public static final String GST_NUMBER = "GST_NUMBER";
	public static final String NIC_CODE = "NIC_2008_ACTIVITY_CODE";

	private static final String MAHARASHTRA_STATE_CODE = "27";
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\d{10}$");
	private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{6}$");
	private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
	private static final Pattern GST_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$");

	private final DistrictMasterRepository districtMasterRepository;
	private final TalukaMasterRepository talukaMasterRepository;
	private final NICCodeRepository nicCodeRepository;
	private final MstRegistryDetailsPageRepository registryRepository;

	public CsvUploadValidationResultDto validate(Map<String, String> rowData, Set<String> seenActKeys, Set<String> seenGstKeys) {
		return validate(rowData, seenActKeys, seenGstKeys, prepareValidationContext(), true);
	}

	public CsvUploadValidationResultDto validate(Map<String, String> rowData, Set<String> seenActKeys, Set<String> seenGstKeys,
			CsvUploadValidationContext context) {
		return validate(rowData, seenActKeys, seenGstKeys, context, true);
	}

	public CsvUploadValidationResultDto validateForPreview(Map<String, String> rowData, Set<String> seenActKeys,
			Set<String> seenGstKeys, CsvUploadValidationContext context) {
		return validate(rowData, seenActKeys, seenGstKeys, context, false);
	}

	public CsvUploadValidationResultDto validateForProcessing(Map<String, String> rowData, CsvUploadValidationContext context) {
		return validate(rowData, null, null, context, true);
	}

	private CsvUploadValidationResultDto validate(Map<String, String> rowData, Set<String> seenActKeys, Set<String> seenGstKeys,
			CsvUploadValidationContext context, boolean checkPersistentDuplicates) {
		List<String> errors = new ArrayList<>();
		boolean duplicate = false;

		validateMandatory(rowData, ESTABLISHMENT_NAME, "Establishment/Owner Name", errors);
		validateMandatory(rowData, DISTRICT, "District", errors);
		validateMandatory(rowData, TALUKA, "Taluka", errors);
		validateMandatory(rowData, ACT_REGISTRATION_NO, "Act/Authority Registration No", errors);
		validateMandatory(rowData, NAME_OF_ACT, "Name of Act", errors);
		validateMandatory(rowData, NIC_CODE, "NIC Code", errors);

		String email = value(rowData, EMAIL);
		if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
			errors.add("Invalid email format");
		}

		String mobileNo = value(rowData, MOBILE_NO);
		if (!mobileNo.isBlank() && !MOBILE_PATTERN.matcher(mobileNo).matches()) {
			errors.add("Mobile number must be 10 digits");
		}

		String pinCode = value(rowData, PIN_CODE);
		if (!pinCode.isBlank() && !PIN_PATTERN.matcher(pinCode).matches()) {
			errors.add("PIN code must be 6 digits");
		}

		String pan = value(rowData, PAN).toUpperCase();
		if (!pan.isBlank() && !PAN_PATTERN.matcher(pan).matches()) {
			errors.add("Invalid PAN format");
		}

		String gstNumber = value(rowData, GST_NUMBER).toUpperCase();
		if (!gstNumber.isBlank() && !GST_PATTERN.matcher(gstNumber).matches()) {
			errors.add("Invalid GST format");
		}

		String nicCode = value(rowData, NIC_CODE);
		if (!nicCode.isBlank() && !context.getNicCodes().contains(normalizeForKey(nicCode))) {
			errors.add("NIC code not found");
		}

		String district = value(rowData, DISTRICT);
		String taluka = value(rowData, TALUKA);
		Optional<String> districtCode = Optional.empty();
		if (!district.isBlank()) {
			districtCode = Optional.ofNullable(context.getDistrictCodesByName().get(normalizeForKey(district)));
			if (districtCode.isEmpty()) {
				errors.add("District not found");
			}
		}

		if (!taluka.isBlank() && districtCode.isPresent() && !context.hasTaluka(districtCode.get(), taluka)) {
			errors.add("Taluka not found for selected district");
		}

		String actKey = buildActDuplicateKey(rowData);
		if (!actKey.isBlank()) {
			boolean duplicateFound = seenActKeys != null && seenActKeys.contains(actKey);
			if (!duplicateFound && checkPersistentDuplicates) {
				duplicateFound = context.isExistingActDuplicate(actKey, value(rowData, ACT_REGISTRATION_NO),
						value(rowData, NAME_OF_ACT));
			}
			if (duplicateFound) {
				duplicate = true;
				errors.add("Duplicate record found for Act/Authority Registration No and Name of Act");
			} else if (seenActKeys != null) {
				seenActKeys.add(actKey);
			}
		}

		String gstKey = gstNumber.replace(" ", "");
		if (!gstKey.isBlank()) {
			boolean duplicateFound = seenGstKeys != null && seenGstKeys.contains(gstKey);
			if (!duplicateFound && checkPersistentDuplicates) {
				duplicateFound = context.isExistingGstDuplicate(gstKey, gstNumber);
			}
			if (duplicateFound) {
				duplicate = true;
				errors.add("Duplicate GST number found");
			} else if (seenGstKeys != null) {
				seenGstKeys.add(gstKey);
			}
		}

		return CsvUploadValidationResultDto.builder().valid(errors.isEmpty()).duplicate(duplicate)
				.errorMessage(errors.isEmpty() ? null : String.join("; ", errors)).build();
	}

	public CsvUploadValidationContext prepareValidationContext() {
		Map<String, String> districtCodesByName = new HashMap<>();
		districtMasterRepository.findByIsActiveTrue().stream()
				.filter(district -> MAHARASHTRA_STATE_CODE.equals(district.getCensusStateCode()))
				.forEach(district -> districtCodesByName.put(normalizeForKey(district.getDistrictName()),
						district.getCensusDistrictCode()));

		Map<String, Set<String>> talukasByDistrictCode = new HashMap<>();
		talukaMasterRepository.findAll().stream().filter(taluka -> Boolean.TRUE.equals(taluka.getIsActive()))
				.forEach(taluka -> talukasByDistrictCode
						.computeIfAbsent(normalizeForKey(taluka.getCensusDistrictCode()), ignored -> new HashSet<>())
						.add(normalizeForKey(taluka.getTalukaName())));

		Set<String> nicCodes = nicCodeRepository.findAll().stream()
				.filter(nicCode -> "Y".equalsIgnoreCase(nicCode.getIsActive()))
				.map(nicCode -> normalizeForKey(nicCode.getCode())).collect(java.util.stream.Collectors.toSet());

		return new CsvUploadValidationContext(districtCodesByName, talukasByDistrictCode, nicCodes,
				new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
	}

	private void validateMandatory(Map<String, String> rowData, String key, String label, List<String> errors) {
		if (value(rowData, key).isBlank()) {
			errors.add(label + " is mandatory");
		}
	}

	private String buildActDuplicateKey(Map<String, String> rowData) {
		String actRegistrationNo = normalizeForKey(value(rowData, ACT_REGISTRATION_NO));
		String actName = normalizeForKey(value(rowData, NAME_OF_ACT));
		if (actRegistrationNo.isBlank() || actName.isBlank()) {
			return "";
		}

		return actRegistrationNo + "|" + actName;
	}

	private String normalizeForKey(String value) {
		return value == null ? "" : value.trim().toUpperCase();
	}

	private String value(Map<String, String> rowData, String key) {
		return rowData.getOrDefault(key, "").trim();
	}

	public class CsvUploadValidationContext {
		private final Map<String, String> districtCodesByName;
		private final Map<String, Set<String>> talukasByDistrictCode;
		private final Set<String> nicCodes;
		private final Map<String, Boolean> existingActDuplicateCache;
		private final Map<String, Boolean> existingGstDuplicateCache;

		public CsvUploadValidationContext(Map<String, String> districtCodesByName,
				Map<String, Set<String>> talukasByDistrictCode, Set<String> nicCodes,
				Map<String, Boolean> existingActDuplicateCache, Map<String, Boolean> existingGstDuplicateCache) {
			this.districtCodesByName = districtCodesByName;
			this.talukasByDistrictCode = talukasByDistrictCode;
			this.nicCodes = nicCodes;
			this.existingActDuplicateCache = existingActDuplicateCache;
			this.existingGstDuplicateCache = existingGstDuplicateCache;
		}

		public boolean hasTaluka(String districtCode, String talukaName) {
			Set<String> talukas = talukasByDistrictCode.getOrDefault(normalize(districtCode), Set.of());
			return talukas.contains(normalize(talukaName));
		}

		public boolean isExistingActDuplicate(String actKey, String actRegistrationNumber, String actName) {
			return existingActDuplicateCache.computeIfAbsent(actKey,
					ignored -> registryRepository.existsDuplicateByActAuthorityRegistrationNumbersAndNameOfAct(
							actRegistrationNumber, actName));
		}

		public boolean isExistingGstDuplicate(String gstKey, String gstNumber) {
			return existingGstDuplicateCache.computeIfAbsent(normalize(gstKey),
					ignored -> registryRepository.existsByGstNumberIgnoreCase(gstNumber));
		}

		private String normalize(String value) {
			return value == null ? "" : value.trim().toUpperCase();
		}

		public Map<String, String> getDistrictCodesByName() {
			return districtCodesByName;
		}

		public Set<String> getNicCodes() {
			return nicCodes;
		}
	}
}
