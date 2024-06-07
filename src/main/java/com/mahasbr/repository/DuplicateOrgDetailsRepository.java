package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.DuplicateOrgDetailsEntity;

public interface DuplicateOrgDetailsRepository  extends JpaRepository<DuplicateOrgDetailsEntity, Long> {
	
}