package com.mahasbr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.CensusEntity;


@Repository
public interface CensusEntityRepository extends JpaRepository<CensusEntity, Long> {

//	Optional<CensusEntity> findByCensusStateNameAndCensusDistrictNameAndCensusTahsilNameAndCensusVillageName(
//			String stateName, String districtName, String talukaName, String villageName);
//	
	
	@Query("""
		    SELECT c FROM CensusEntity c
		    WHERE LOWER(c.censusStateName) = LOWER(:stateName)
		      AND LOWER(c.censusDistrictName) = LOWER(:districtName)
		      AND LOWER(c.censusTahsilName) = LOWER(:talukaName)
		      AND LOWER(c.censusVillageName) = LOWER(:villageName)
		""")
		Optional<CensusEntity> findMatchIgnoreCase(
		        @Param("stateName") String stateName,
		        @Param("districtName") String districtName,
		        @Param("talukaName") String talukaName,
		        @Param("villageName") String villageName
		);

	
	//
	//@Query("SELECT v FROM VillageMaster v WHERE v.nameOfEstateOwner = :#{#details.nameOfEstateOwner} AND v.townVillage = :#{#details.townVillage}");

}
