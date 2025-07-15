package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "district_master", indexes = { @Index(name = "idx_census_district_code", columnList = "census_district_code"),
		@Index(name = "idx_census_state_code_fk", columnList = "census_state_code") })
public class DistrictMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "district_master_seq_gen")
	@SequenceGenerator(name = "district_master_seq_gen", sequenceName = "district_seq", allocationSize = 1)
	@Column(name = "district_id")
	private Long districtId;

	@NotBlank(message = "District code is mandatory")
	@Column(name = "census_district_code", unique = true, nullable = false, updatable = false)
	private String censusDistrictCode;

	@NotBlank(message = "District name is mandatory")
	@Column(name = "district_name", nullable = false)
	private String districtName;


	@NotBlank(message = "Census state code is mandatory")
	@Column(name = "census_state_code", nullable = false)
	private String censusStateCode; // FK to StatesMaster.censusStateCode
	
	@NotBlank(message = "Division is mandatory")
	@Column(name = "division_code")
	private String divisionCode; 

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}