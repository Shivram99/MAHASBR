package com.mahasbr.entity;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "master_nic_details")
public class MasterNicDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "master_nic_details_generator")
	@SequenceGenerator(name = "master_nic_details_generator", sequenceName = "master_nic_details_seq", allocationSize = 1)
	@NotBlank
	private Long id;
	
	@Column(name = "nic_section_code", nullable = false)
	private String nicSectionCode;

	@Column(name = "nic_section_name", nullable = false)
	private String nicSectionName;

	
	
	@Column(name = "nic_division_code", nullable = false)
	private String nicDivisionCode;

	@Column(name = "nic_division_name", nullable = false)
	private String nicDivisionName;

	
	
	@Column(name = "nic_group_code", nullable = false)
	private String nicGroupCode;

	@Column(name = "nic_group_name", nullable = false)
	private String nicGroupName;

	
	
	@Column(name = "nic_class_code", nullable = false)
	private String nicClassCode;

	@Column(name = "nic_class_name", nullable = false)
	private String nicClassName;
	
	
	@Column(name = "nic_code", nullable = false)
	private String nicCode;

	@Column(name = "nic_name", nullable = false)
	private String nicName;
	

	@Column(name = "nic_start_year", nullable = false)
	private int nicStartYear;
	
}

