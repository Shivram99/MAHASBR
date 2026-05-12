package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Repository
public interface DistrictMasterRepository extends JpaRepository<DistrictMaster, Long> {

	Optional<DistrictMaster> findByCensusDistrictCode(String censusDistrictCode);

	@Query("SELECT d FROM DistrictMaster d WHERE d.isActive = true")
	List<DistrictMaster> findByIsActiveTrue();

	@Query("SELECT d.districtName FROM DistrictMaster d WHERE d.censusDistrictCode IN :censusDistrictCodes")
	List<String> findDistrictNamesByCensusDistrictCodes(List<Long> censusDistrictCodes);

	// IGNORE CASE for districtName only (stateCode remains exact as your
	// requirement)
	@Query("""
			SELECT d.censusDistrictCode
			FROM DistrictMaster d
			WHERE LOWER(d.districtName) = LOWER(:districtName)
			  AND d.censusStateCode = :censusStateCode
			""")
	Optional<String> findCensusDistrictCodeByNameAndState(@Param("districtName") String districtName,
			@Param("censusStateCode") String censusStateCode);

	// Make divisionCode ALSO ignore case (without changing parameter name/type)
	@Query("SELECT d.districtName FROM DistrictMaster d WHERE LOWER(d.divisionCode) = LOWER(:divisionCode) AND d.isActive = true")
	List<String> findDistrictNamesByDivisionCode(@Param("divisionCode") String divisionCode);

	@Query("SELECT d FROM DistrictMaster d WHERE LOWER(d.divisionCode) = LOWER(:divisionCode) AND d.isActive = true")
	List<DistrictMaster> findByDivisionCodeAndIsActiveTrue(@Param("divisionCode") String divisionCode);

//	@Query("SELECT d.districtName FROM DistrictMaster d WHERE d.censusDistrictCode = :censusDistrictCode AND d.isActive = true")
//	Optional<String> findDistrictNameById(@Param("censusDistrictCode") Long districtId);
	
	@Query("SELECT d.districtName FROM DistrictMaster d WHERE d.districtId = :districtId AND d.isActive = true")
	Optional<String> findDistrictNameById(@Param("districtId") Long districtId);
	
	@Query("""
			SELECT m FROM MstRegistryDetailsPageEntity m
			WHERE LOWER(m.district) = :district
			AND (:cursor IS NULL OR m.id < :cursor)
			ORDER BY m.id DESC
			""")
			Slice<MstRegistryDetailsPageEntity> findNextByDistrict(
			        @Param("district") String district,
			        @Param("cursor") Long cursor,
			        Pageable pageable
			);


}
