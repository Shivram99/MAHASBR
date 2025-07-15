package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;

@Repository
public interface ConcernRegistryDetailsPageRepository extends JpaRepository<ConcernRegistryDetailsPageEntity, Long> {

}
