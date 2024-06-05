package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.VillageMaster;

@Repository
public interface VillageMasterRepository extends JpaRepository<VillageMaster, Long> {

	@Query(value = "SELECT * FROM village_master WHERE  CENSUS_TALUKA_CODE = :censusTalukaCode", nativeQuery = true)
	List<VillageMaster> findBycensusTalukaCode(long censusTalukaCode);

}
