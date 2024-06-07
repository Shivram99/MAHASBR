package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DetailsPage;

@Repository
public interface DetailsPageRepository extends JpaRepository<DetailsPage, Long> {
	/*
	 * 
	 * @Query("FROM DetailsPage t WHERE t.nameOfEstateOwner=?1 and t.houseNo=?2 AND t.StreetName=?3 AND t.locality=?4  AND t.taluka=?5  AND t.district=?6  AND t.sector=?7 AND t.nameofAuth=?8  AND t.nameofAct=?9"
	 * ) DetailsPage getDetailsByColumn(String nameOfEstateOwner, String houseNo,
	 * String streetName, String locality, String townVillage, String taluka, String
	 * district, int pincode, String sector, String nameofAuth, String nameofAct);
	 */
	
	
	
	/*
	 * @Query("FROM DetailsPage t WHERE UPPER(t.nameOfEstateOwner) = UPPER(?1) AND UPPER(t.houseNo) = UPPER(?2) AND UPPER(t.streetName) = UPPER(?3) AND UPPER(t.locality) = UPPER(?4) AND UPPER(t.townVillage) = UPPER(?5) AND UPPER(t.taluka) = UPPER(?6) AND UPPER(t.district) = UPPER(?7) AND UPPER(t.sector) = UPPER(?9) AND UPPER(t.nameofAuth) = UPPER(?10) AND UPPER(t.nameofAct) = UPPER(?11)"
	 * ) DetailsPage getDetailsByColumn(String nameOfEstateOwner, String houseNo,
	 * String streetName, String locality, String townVillage, String taluka, String
	 * district, int pincode, String sector, String nameofAuth, String nameofAct);
	 */
	
	
	  @Query("FROM DetailsPage t WHERE UPPER(t.nameOfEstateOwner) = UPPER(:nameOfEstateOwner) AND " +
	            "UPPER(t.houseNo) = UPPER(:houseNo) AND UPPER(t.streetName) = UPPER(:streetName) AND " +
	            "UPPER(t.locality) = UPPER(:locality) AND UPPER(t.townVillage) = UPPER(:townVillage) AND " +
	            "UPPER(t.taluka) = UPPER(:taluka) AND UPPER(t.district) = UPPER(:district) AND " +
	            "UPPER(t.sector) = UPPER(:sector) AND UPPER(t.nameofAuth) = UPPER(:nameofAuth) AND " +
	            "UPPER(t.nameofAct) = UPPER(:nameofAct)")
	    DetailsPage getDetailsByColumn(@Param("nameOfEstateOwner") String nameOfEstateOwner,
	                                   @Param("houseNo") String houseNo,
	                                   @Param("streetName") String streetName,
	                                   @Param("locality") String locality,
	                                   @Param("townVillage") String townVillage,
	                                   @Param("taluka") String taluka,
	                                   @Param("district") String district,
	                                   @Param("sector") String sector,
	                                   @Param("nameofAuth") String nameofAuth,
	                                   @Param("nameofAct") String nameofAct);


}
