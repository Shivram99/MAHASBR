package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.Taluka;

public interface TalukaRepository extends JpaRepository<Taluka, Long> {

	List<Taluka> findByDistrictId(Long districtId);}