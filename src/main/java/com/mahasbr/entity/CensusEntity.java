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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "census_master")
public class CensusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "census_master_seq_generator")
	@SequenceGenerator(name = "census_master_seq_generator", sequenceName = "census_seq", allocationSize = 1)
	@NotBlank
	private Long id;

	@NotBlank
	private String censusStateCode;

	@NotBlank
	private String censusStateName;

	@NotBlank
	private String censusDistrictCode;

	@NotBlank
	private String censusDistrictName;

	@NotBlank
	private String censusTahsilCode;

	@NotBlank
	private String censusTahsilName;

	@NotBlank
	private String censusVillageCode;

	@NotBlank
	private String censusVillageName;

}
