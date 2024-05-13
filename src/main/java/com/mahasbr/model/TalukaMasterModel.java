package com.mahasbr.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class TalukaMasterModel {
	@NotBlank
	@Size(max = 100)
	private String TalukaName;
	@NotBlank
	@Size(max = 10)
	private int censusTalukaCode;
	public String getTalukaName() {
		return TalukaName;
	}
	public void setTalukaName(String talukaName) {
		TalukaName = talukaName;
	}
	public int getCensusTalukaCode() {
		return censusTalukaCode;
	}
	public void setCensusTalukaCode(int censusTalukaCode) {
		this.censusTalukaCode = censusTalukaCode;
	}
	
	
	
}

