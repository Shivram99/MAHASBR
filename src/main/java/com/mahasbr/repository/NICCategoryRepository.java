package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.NICCategoryEntity;

public interface NICCategoryRepository extends JpaRepository<NICCategoryEntity, String> {

	List<NICCategoryEntity> findAll();
}