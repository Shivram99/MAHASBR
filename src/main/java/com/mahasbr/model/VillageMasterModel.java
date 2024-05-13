package com.mahasbr.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VillageMasterModel {
	@NotBlank
	@Size(max = 100)
	private String villageName;
	@NotBlank
	@Size(max = 10)
	private Integer censusVillageCode;
	public String getVillageName() {
		return villageName;
	}
	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}
	public Integer getCensusVillageCode() {
		return censusVillageCode;
	}
	public void setCensusVillageCode(Integer censusVillageCode) {
		this.censusVillageCode = censusVillageCode;
	}
	
	
	
}

