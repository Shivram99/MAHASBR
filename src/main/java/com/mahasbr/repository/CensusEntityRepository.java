package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.CensusEntity;


@Repository
public interface CensusEntityRepository extends JpaRepository<CensusEntity, Long> {
	
	
	//
	//@Query("SELECT v FROM VillageMaster v WHERE v.nameOfEstateOwner = :#{#details.nameOfEstateOwner} AND v.townVillage = :#{#details.townVillage}");

}
