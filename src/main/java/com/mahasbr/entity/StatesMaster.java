package com.mahasbr.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "state_master", indexes = { @Index(name = "idx_census_state_code", columnList = "census_state_code") })
public class StatesMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_master_seq_gen")
	@SequenceGenerator(name = "state_master_seq_gen", sequenceName = "state_seq", allocationSize = 1)
	@Column(name = "state_id")
	private Long stateId;


	@NotBlank(message = "Census state code is mandatory")
	@Column(name = "census_state_code", unique = true, nullable = false, updatable = false)
	private String censusStateCode;

	@NotBlank(message = "State name is mandatory")
	@Column(name = "state_name", nullable = false)
	private String stateName;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}