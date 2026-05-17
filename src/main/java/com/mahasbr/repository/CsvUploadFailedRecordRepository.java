package com.mahasbr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.CsvUploadFailedRecordEntity;

public interface CsvUploadFailedRecordRepository extends JpaRepository<CsvUploadFailedRecordEntity, Long> {

	Page<CsvUploadFailedRecordEntity> findByJobIdOrderByRowNumberAsc(String jobId, Pageable pageable);

	long countByJobId(String jobId);

	void deleteByJobId(String jobId);
}
