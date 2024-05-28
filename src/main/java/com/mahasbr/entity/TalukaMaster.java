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
@Table(name = "taluka_master")
public class TalukaMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taluka_master_seq_generator")
	@SequenceGenerator(name = "taluka_master_seq_generator", sequenceName = "taluka_seq", allocationSize = 1)
	@NotBlank
	private Long censusTalukaCode;

	@NotBlank
	private String talukaName;

	@NotBlank
	private Long censusDistrictCode;

<<<<<<< HEAD
=======
	public Long getCensusTalukaCode() {
		return censusTalukaCode;
	}

	public void setCensusTalukaCode(Long censusTalukaCode) {
		this.censusTalukaCode = censusTalukaCode;
	}

	public String getTalukaName() {
		return talukaName;
	}

	public void setTalukaName(String talukaName) {
		this.talukaName = talukaName;
	}

	public Long getCensusDistrictCode() {
		return censusDistrictCode;
	}

	public void setCensusDistrictCode(Long censusDistrictCode) {
		this.censusDistrictCode = censusDistrictCode;
	}
	
	
	
	
>>>>>>> 59dabe76fb422650d341e362819d296c5542fa3d
}
