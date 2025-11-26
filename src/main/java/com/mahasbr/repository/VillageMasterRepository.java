package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.VillageMaster;

@Repository
public interface VillageMasterRepository extends JpaRepository<VillageMaster, Long> {

	
	@Query("FROM VillageMaster t")
	List<VillageMaster> getVillageDtlByVillageName(DetailsPage details);


	@Query("FROM VillageMaster t WHERE  t.censusTalukaCode=?1")
	List<VillageMaster> findByCensusTalukaCode(String censusTalukaCode);


	Optional<VillageMaster> findByCensusVillageCode(String censusVillageCode);
	
	@Query("""
		    SELECT v FROM VillageMaster v
		    WHERE LOWER(v.censusDistrictCode) = LOWER(:districtCode)
		      AND LOWER(v.censusTalukaCode) = LOWER(:talukaCode)
		      AND LOWER(v.villageName) = LOWER(:villageName)
		""")
		Optional<VillageMaster> findByCensusDistrictCodeAndCensusTalukaCodeAndVillageNameIgnoreCase(
		        @Param("districtCode") String censusDistrictCode,
		        @Param("talukaCode") String censusTalukaCode,
		        @Param("villageName") String villageName
		);


//	Optional<VillageMaster> getVillagesByCensusTalukaCodeAndVillageName(Long censusTalukaCode, String villageName);


}
