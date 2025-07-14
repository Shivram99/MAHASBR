package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DistrictMaster;

@Repository
public interface DistrictMasterRepository extends JpaRepository<DistrictMaster, Long> {

	Optional<DistrictMaster> findByCensusDistrictCode(String censusDistrictCode);
	
	@Query("SELECT d FROM DistrictMaster d WHERE d.isActive = true")
	List<DistrictMaster> findByIsActiveTrue();


}
