package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.NICCodeEntity;

public interface NICCodeRepository extends JpaRepository<NICCodeEntity, Long> {
    // Additional query methods (if needed) can be defined here
}
