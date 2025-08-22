package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.MstMenuEntity;

public interface MstMenuRepository extends JpaRepository<MstMenuEntity, Integer> {
    boolean existsByMenuCode(Integer menuCode);
}
