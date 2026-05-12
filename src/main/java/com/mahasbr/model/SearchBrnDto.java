package com.mahasbr.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchBrnDto {

	@NotNull(message = "District is required.")
	private Long districtId;

	private Long talukaId;

	private String establishmentName;

	@Pattern(regexp = "^[0-9]{16}$", message = "BRN No must be exactly 16 digits.")
	private String brn;

}
