package com.mahasbr.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "csv_upload_failed_record", indexes = {
		@Index(name = "idx_csv_upload_failed_job_id", columnList = "job_id"),
		@Index(name = "idx_csv_upload_failed_brn", columnList = "brn")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvUploadFailedRecordEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "csv_upload_failed_seq_gen")
	@SequenceGenerator(name = "csv_upload_failed_seq_gen", sequenceName = "csv_upload_failed_record_seq", allocationSize = 1)
	private Long id;

	@Column(name = "job_id", nullable = false, length = 64)
	private String jobId;

	@Column(name = "row_number", nullable = false)
	private int rowNumber;

	@Column(name = "establishment_name", length = 500)
	private String establishmentName;

	@Column(name = "brn", length = 50)
	private String brn;

	@Column(name = "error_message", length = 2000)
	private String errorMessage;

	@Lob
	@Column(name = "raw_data")
	private String rawData;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@PrePersist
	void onCreate() {
		if (createdDate == null) {
			createdDate = LocalDateTime.now();
		}
	}
}
