package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.TalukaMaster;

@Repository
public interface TalukaMasterRepository extends JpaRepository<TalukaMaster, Long> {

	List<TalukaMaster> findByCensusDistrictCode(Long censusDistrictCode);


	@Query(value = "SELECT * FROM taluka_master WHERE  CENSUS_DISTRICT_CODE = :censusDistrictCode", nativeQuery = true)
	List<TalukaMaster> findBycensusDistrictCode(long censusDistrictCode);

}
