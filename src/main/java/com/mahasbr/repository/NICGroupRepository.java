package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.NICGroupEntity;

public interface NICGroupRepository extends JpaRepository<NICGroupEntity, String> {
    // Additional query methods (if needed) can be defined here
}
