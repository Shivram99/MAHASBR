package com.mahasbr.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor  
@Table(name = "district_master")
public class DistrictMaster  extends Auditable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "district_master_seq_generator")
    @SequenceGenerator(name="district_master_seq_generator", sequenceName = "district_seq", allocationSize=1)
	@NotBlank
	private Long censusDistrictCode;
	

	@NotBlank
	private String districtName;
	
	
	@NotBlank
	private Long censusStateCode;


	public Long getCensusDistrictCode() {
		return censusDistrictCode;
	}


	public void setCensusDistrictCode(Long censusDistrictCode) {
		this.censusDistrictCode = censusDistrictCode;
	}


	public String getDistrictName() {
		return districtName;
	}


	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}


	public Long getCensusStateCode() {
		return censusStateCode;
	}


	public void setCensusStateCode(Long censusStateCode) {
		this.censusStateCode = censusStateCode;
	}
	
	
	
}
