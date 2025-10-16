package com.mahasbr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.DivisionMaster;

public interface DivisionRepository extends JpaRepository<DivisionMaster, Long> {
	Optional<DivisionMaster> findByDivisionCode(String divisionCode);

	boolean existsByDivisionCode(String divisionCode);
}
