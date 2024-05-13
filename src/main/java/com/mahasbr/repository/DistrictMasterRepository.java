package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DistrictMaster;

@Repository
public interface DistrictMasterRepository extends JpaRepository<DistrictMaster, Long> {

}
