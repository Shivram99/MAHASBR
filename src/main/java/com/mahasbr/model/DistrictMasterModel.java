package com.mahasbr.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictMasterModel {
	
	
	@NotBlank
	@Size(max = 100)
	private String districtName;
	
	
	@NotBlank
	@Size(max = 10)
	private int censusDistrictCode;
	
	
	
	
	
	
}

