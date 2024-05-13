package com.mahasbr.entity;

import com.mahasbr.model.VillageMasterModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "village_master")
public class VillageMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotBlank
	private Long villageId;

	@NotBlank
	private String villageName;
	@NotBlank
	private Integer censusVillageCode;

	
	
	public Long getVillageId() {
		return villageId;
	}



	public void setVillageId(Long villageId) {
		this.villageId = villageId;
	}



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

	public VillageMaster(VillageMasterModel villageMasterModel) {
		this.villageName = villageMasterModel.getVillageName();
		this.censusVillageCode = villageMasterModel.getCensusVillageCode();

	}

	
}
