package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DistrictMaster;

@Repository
public interface DistrictMasterRepository extends JpaRepository<DistrictMaster, Long> {

	Optional<DistrictMaster> findByCensusDistrictCode(String censusDistrictCode);

	@Query("SELECT d FROM DistrictMaster d WHERE d.isActive = true")
	List<DistrictMaster> findByIsActiveTrue();

	// Optional<DistrictMaster> findByCensusStateCodeAndDistrictName(Long
	// censusStateCode, String districtName);

	@Query("SELECT d.districtName FROM DistrictMaster d WHERE d.censusDistrictCode IN :censusDistrictCodes")
	List<String> findDistrictNamesByCensusDistrictCodes(List<Long> censusDistrictCodes);

	@Query("SELECT d.censusDistrictCode " +
		       "FROM DistrictMaster d " +
		       "WHERE d.districtName = :districtName " +
		       "AND d.censusStateCode = :censusStateCode")
		Optional<String> findCensusDistrictCodeByNameAndState(@Param("districtName") String districtName,
		                                                     @Param("censusStateCode") String censusStateCode);
	
	 // Get district names by divisionCode
    @Query("SELECT d.districtName FROM DistrictMaster d WHERE d.divisionCode = :divisionCode AND d.isActive = true")
    List<String> findDistrictNamesByDivisionCode(@Param("divisionCode") String divisionCode);

    // If you need full entities instead of just names
    @Query("SELECT d FROM DistrictMaster d WHERE d.divisionCode = :divisionCode AND d.isActive = true")
    List<DistrictMaster> findByDivisionCodeAndIsActiveTrue(@Param("divisionCode") String divisionCode);
    
    @Query("SELECT d.districtName FROM DistrictMaster d WHERE d.censusDistrictCode = :censusDistrictCode AND d.isActive = true")
    Optional<String> findDistrictNameById(@Param("censusDistrictCode") Long districtId);


}
