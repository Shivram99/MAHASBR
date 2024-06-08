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
@Table(name = "VILLAGE_SEQUENCE")
public class VillageSequenceMaster {
	

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VILLAGE_SEQUENCE_seq_generator")
	@SequenceGenerator(name = "VILLAGE_SEQUENCE_seq_generator", sequenceName = "VILLAGE_SEQUENCE_seq", allocationSize = 1)
	private Long currentSequence;

	@NotBlank
	private String villageName;
	
	
	@NotBlank
	private String censusVillageCode;
	
	
	
	
	


}
