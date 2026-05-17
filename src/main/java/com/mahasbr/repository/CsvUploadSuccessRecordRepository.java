package com.mahasbr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.CsvUploadSuccessRecordEntity;

public interface CsvUploadSuccessRecordRepository extends JpaRepository<CsvUploadSuccessRecordEntity, Long> {

	Page<CsvUploadSuccessRecordEntity> findByJobIdOrderByRowNumberAsc(String jobId, Pageable pageable);

	long countByJobId(String jobId);

	void deleteByJobId(String jobId);
}
