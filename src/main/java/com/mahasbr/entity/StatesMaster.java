package com.mahasbr.entity;

import com.mahasbr.model.StatesMasterModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "states_master")
public class StatesMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotBlank
	private Long stateId;

	@NotBlank
	private String stateName;
	@NotBlank
	private Integer censusStateCode;

	public StatesMaster(StatesMasterModel stateMasterModel) {
		this.stateName = stateMasterModel.getStateName();
		this.censusStateCode = stateMasterModel.getCensusStateCode();

	}

}
