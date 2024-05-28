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
<<<<<<< HEAD
@AllArgsConstructor
@Table(name = "states_master")
=======
@AllArgsConstructor  
@Table(name = "state_master")
>>>>>>> 59dabe76fb422650d341e362819d296c5542fa3d
public class StatesMaster extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_master_seq_generator")
	@SequenceGenerator(name = "state_master_seq_generator", sequenceName = "state_seq", allocationSize = 1)
	@NotBlank
	private Integer censusStateCode;

	@NotBlank
	private String stateName;

}
