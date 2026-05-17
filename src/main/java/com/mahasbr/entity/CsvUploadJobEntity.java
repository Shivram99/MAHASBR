package com.mahasbr.entity;

import java.time.LocalDateTime;

import com.mahasbr.service.CsvUploadLifecycleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "csv_upload_job", indexes = {
		@Index(name = "idx_csv_upload_job_job_id", columnList = "job_id"),
		@Index(name = "idx_csv_upload_job_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvUploadJobEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "csv_upload_job_seq_gen")
	@SequenceGenerator(name = "csv_upload_job_seq_gen", sequenceName = "csv_upload_job_seq", allocationSize = 1)
	private Long id;

	@Column(name = "job_id", nullable = false, unique = true, length = 64)
	private String jobId;

	@Column(name = "file_name", nullable = false, length = 255)
	private String fileName;

	@Column(name = "stored_file_name", nullable = false, length = 255)
	private String storedFileName;

	@Column(name = "file_size", nullable = false)
	private long fileSize;

	@Column(name = "total_records", nullable = false)
	private int totalRecords;

	@Column(name = "processed_records", nullable = false)
	private int processedRecords;

	@Column(name = "success_records", nullable = false)
	private int successRecords;

	@Column(name = "failed_records", nullable = false)
	private int failedRecords;

	@Column(name = "pending_records", nullable = false)
	private int pendingRecords;

	@Column(name = "progress_percentage", nullable = false)
	private int progressPercentage;

	@Column(name = "valid_records", nullable = false)
	private int validRecords;

	@Column(name = "invalid_records", nullable = false)
	private int invalidRecords;

	@Column(name = "duplicate_records", nullable = false)
	private int duplicateRecords;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private CsvUploadLifecycleStatus status;

	@Column(name = "preview_ready", nullable = false)
	private boolean previewReady;

	@Column(name = "pause_requested", nullable = false)
	private boolean pauseRequested;

	@Column(name = "cancel_requested", nullable = false)
	private boolean cancelRequested;

	@Column(name = "last_processed_valid_record", nullable = false)
	private int lastProcessedValidRecord;

	@Column(name = "preview_page_size", nullable = false)
	private int previewPageSize;

	@Column(name = "result_page_size", nullable = false)
	private int resultPageSize;

	@Column(name = "total_preview_pages", nullable = false)
	private int totalPreviewPages;

	@Column(name = "total_success_pages", nullable = false)
	private int totalSuccessPages;

	@Column(name = "total_failed_pages", nullable = false)
	private int totalFailedPages;

	@Column(name = "message", length = 1000)
	private String message;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "paused_at")
	private LocalDateTime pausedAt;

	@Column(name = "resumed_at")
	private LocalDateTime resumedAt;

	@Column(name = "created_by", length = 100)
	private String createdBy;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdDate = now;
		updatedDate = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedDate = LocalDateTime.now();
	}
}
