package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.NICClassEntity;

public interface NICClassRepository extends JpaRepository<NICClassEntity, String> {
    //  Fetch all classes belonging to a specific group
    List<NICClassEntity> findByGroup_GroupCode(String groupCode);

}
