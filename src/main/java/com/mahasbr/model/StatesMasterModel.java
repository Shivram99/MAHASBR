package com.mahasbr.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatesMasterModel {
	@NotBlank
	@Size(max = 100)
	private String stateName;
	@NotBlank
	@Size(max = 10)
	private Integer censusStateCode;
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public Integer getCensusStateCode() {
		return censusStateCode;
	}
	public void setCensusStateCode(Integer censusStateCode) {
		this.censusStateCode = censusStateCode;
	}
	
	
	
}
