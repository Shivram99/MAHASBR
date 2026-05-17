package com.mahasbr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.CsvUploadJobEntity;

public interface CsvUploadJobRepository extends JpaRepository<CsvUploadJobEntity, Long> {

	Optional<CsvUploadJobEntity> findByJobId(String jobId);
}
