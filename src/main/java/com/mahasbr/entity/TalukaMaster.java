package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "taluka_master", indexes = { @Index(name = "idx_census_taluka_code", columnList = "census_taluka_code"),
		@Index(name = "idx_census_district_code_fk", columnList = "census_district_code") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_district_taluka_code", columnNames = { "census_district_code",
						"census_taluka_code" }) })
public class TalukaMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taluka_master_seq_gen")
	@SequenceGenerator(name = "taluka_master_seq_gen", sequenceName = "taluka_seq", allocationSize = 1)
	@Column(name = "taluka_id")
	private Long talukaId;

	@NotBlank(message = "Taluka code is mandatory")
	@Column(name = "census_taluka_code", nullable = false, updatable = false)
	private String censusTalukaCode;

	@NotBlank(message = "Taluka name is mandatory")
	@Column(name = "taluka_name", nullable = false)
	private String talukaName;

	@NotBlank(message = "District code is mandatory")
	@Column(name = "census_district_code")
	private String censusDistrictCode; // FK to DistrictMaster.districtCode

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}
