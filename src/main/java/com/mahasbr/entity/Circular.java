package com.mahasbr.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "circulars")
public class Circular extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "circular_seq_generator")
	@SequenceGenerator(name = "circular_seq_generator", sequenceName = "circular_seq", allocationSize = 1)
	private Long id;

	@NotBlank
	private String fileName;

	@NotBlank
	private String filePath;

	public Circular(String fileName, String filePath) {
		this.fileName = fileName;
		this.filePath = filePath;
	}

}
