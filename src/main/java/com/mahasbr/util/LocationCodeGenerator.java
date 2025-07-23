package com.mahasbr.util;

public class LocationCodeGenerator {

	public static class Result {
		private final boolean success;
		private final String message;

		public Result(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}
	}

	/**
	 * Generates a 16-digit location code (SSDDDTTTTTVVVVVV) after validation.
	 *
	 * @param stateCode         2-digit state code (01-99)
	 * @param districtCode      3-digit district code (001-999)
	 * @param subDistrictCode   5-digit sub-district code (00001-99999)
	 * @param villageOrTownCode 6-digit code (000001-799999 for villages,
	 *                          800001-899999 for towns)
	 * @return Result object with success flag and either the generated code or
	 *         validation message.
	 */
	public static Result generateLocationCode(int stateCode, int districtCode, int subDistrictCode,
			int villageOrTownCode) {
		// Validate inputs
		if (stateCode < 1 || stateCode > 99) {
			return new Result(false, "Invalid State Code: must be between 01 and 99");
		}
		if (districtCode < 1 || districtCode > 999) {
			return new Result(false, "Invalid District Code: must be between 001 and 999");
		}
		if (subDistrictCode < 1 || subDistrictCode > 99999) {
			return new Result(false, "Invalid Sub-District Code: must be between 00001 and 99999");
		}
		if (!((villageOrTownCode >= 1 && villageOrTownCode <= 799999)
				|| (villageOrTownCode >= 800001 && villageOrTownCode <= 899999))) {
			return new Result(false,
					"Invalid Village/Town Code: must be between 000001-799999 (villages) or 800001-899999 (towns)");
		}

		// Build the 16-digit code (zero-padded)
		String locationCode = String.format("%02d%03d%05d%06d", stateCode, districtCode, subDistrictCode,
				villageOrTownCode);

		return new Result(true, locationCode);
	}
}
