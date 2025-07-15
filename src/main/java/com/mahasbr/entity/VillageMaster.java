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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "village_master", indexes = {
		@Index(name = "idx_census_village_code", columnList = "census_village_code"),
		@Index(name = "idx_census_taluka_code_fk", columnList = "census_taluka_code") })
public class VillageMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "village_master_seq_gen")
	@SequenceGenerator(name = "village_master_seq_gen", sequenceName = "village_seq", allocationSize = 1)
	@Column(name = "village_id")
	private Long villageId;

	@NotNull(message = "Village code is mandatory")
	@Column(name = "census_village_code", unique = true, nullable = false, updatable = false)
	private String censusVillageCode;

	@NotBlank(message = "Village name is mandatory")
	@Column(name = "village_name", nullable = false)
	private String villageName;

	@NotBlank(message = "Taluka code is mandatory")
	@Column(name = "census_taluka_code", nullable = false)
	private String censusTalukaCode; // FK to TalukaMaster.talukaCode

	@NotBlank(message = "District code is mandatory")
	@Column(name = "census_district_code")
	private String censusDistrictCode;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}
