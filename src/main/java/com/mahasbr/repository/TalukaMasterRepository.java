package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.TalukaMaster;

@Repository
public interface TalukaMasterRepository extends JpaRepository<TalukaMaster, Long> {

	List<TalukaMaster> findByCensusDistrictCode(Long censusDistrictCode);


	@Query(value = "SELECT * FROM taluka_master WHERE  CENSUS_DISTRICT_CODE = :censusDistrictCode", nativeQuery = true)
	List<TalukaMaster> findBycensusDistrictCode(long censusDistrictCode);


	Optional<TalukaMaster> findByCensusDistrictCodeAndTalukaName(Long censusDistrictCode, String talukaName);


	List<TalukaMaster> findByCensusDistrictCodeIn(List<Long> censusDistrictCode);


	  @Query("SELECT t.talukaName FROM TalukaMaster t WHERE t.censusTalukaCode IN :censusTalukaCodes")
      List<String> findTalukaNameByCensusTalukaCode(List<Long> censusTalukaCodes);

	//Optional<TalukaMaster> findByCensusDistrictCodeAndTalukaName(String censusDistrictCode, String talukaName);

}
