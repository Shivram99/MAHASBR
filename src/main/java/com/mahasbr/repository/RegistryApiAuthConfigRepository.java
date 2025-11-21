package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.RegistryApiAuthConfigEntity;

@Repository
public interface RegistryApiAuthConfigRepository extends JpaRepository<RegistryApiAuthConfigEntity, Long> {

    Optional<RegistryApiAuthConfigEntity> findByServiceNameCode(String serviceNameCode);

    List<RegistryApiAuthConfigEntity> findByActiveTrue();
}
