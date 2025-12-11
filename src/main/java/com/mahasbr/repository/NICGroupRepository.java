package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.NICGroupEntity;
@Repository
public interface NICGroupRepository extends JpaRepository<NICGroupEntity, String> {

    // 🔹 Fetch groups by division code
    List<NICGroupEntity> findByDivision_DivisionCode(String divisionCode);

    // 🔹 Fetch only active groups
    List<NICGroupEntity> findByIsActive(String isActive);

    // 🔹 Fetch active groups under a division
    List<NICGroupEntity> findByDivision_DivisionCodeAndIsActive(String divisionCode, String isActive);

    // 🔹 Optional: Search support
    List<NICGroupEntity> findByGroupCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String code, String description);
}