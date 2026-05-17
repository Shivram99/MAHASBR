package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvUploadRecordDto {
	private int rowNumber;
	private String establishmentName;
	private String district;
	private String taluka;
	private String mobileNo;
	private String email;
	private String pan;
	private String gstNumber;
	private String nicCode;
	private boolean valid;
	private String errorMessage;
}
