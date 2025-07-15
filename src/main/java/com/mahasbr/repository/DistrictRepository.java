package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long> {

	List<District> findByStateId(Long stateId);
	
}
