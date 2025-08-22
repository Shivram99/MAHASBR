package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAll();
}