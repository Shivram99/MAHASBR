package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.NICClassEntity;

public interface NICClassRepository extends JpaRepository<NICClassEntity, String> {
    // Additional query methods (if needed) can be defined here
}
