package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.Village;

public interface VillageRepository extends JpaRepository<Village, Long> {

	List<Village> findByTalukaId(Long talukaId);}
