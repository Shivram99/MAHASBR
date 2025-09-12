package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.RegistryMasterEntity;

@Repository
public interface RegistryMasterRepository extends JpaRepository<RegistryMasterEntity, Long>{

}
