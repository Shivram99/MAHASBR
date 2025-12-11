package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mahasbr.entity.NICCategoryEntity;

public interface NICCategoryRepository extends JpaRepository<NICCategoryEntity, String> {

	List<NICCategoryEntity> findAll();

	 // Fetch only top-level fields (no child loading)
    @Query("SELECT new com.mahasbr.entity.NICCategoryEntity(c.categoryCode, c.description, c.isActive) " +
           "FROM NICCategoryEntity c")
    List<NICCategoryEntity> findBasicCategories();

    // Fetch only active categories (optional)
    @Query("SELECT new com.mahasbr.entity.NICCategoryEntity(c.categoryCode, c.description, c.isActive) " +
           "FROM NICCategoryEntity c WHERE c.isActive = 'Y'")
    List<NICCategoryEntity> findActiveCategories();

}