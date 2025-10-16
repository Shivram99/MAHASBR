package com.mahasbr.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "circular")
public class Circular extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "circulars_seq_generator")
	@SequenceGenerator(name = "circulars_seq_generator", sequenceName = "circulars_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String subject;

	@Column(name = "circular_date",nullable = false)
	private LocalDate circularDate;

	@Column(name = "file_path", nullable = false)
	private String filePath; // store uploaded PDF path

}
