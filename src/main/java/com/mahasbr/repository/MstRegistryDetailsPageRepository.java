package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Repository
public interface MstRegistryDetailsPageRepository extends JpaRepository<MstRegistryDetailsPageEntity, Long> {

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m "
			+ "WHERE m.nameOfEstablishmentOrOwner = :nameOfEstablishmentOrOwner " + "AND m.houseNo = :houseNo "
			+ "AND m.streetName = :streetName " + "AND m.locality = :locality " + "AND m.pinCode = :pinCode "
			+ "AND m.panNumber = :panNumber " + "AND m.tanNumber = :tanNumber "
			+ "AND m.telephoneMobNumber = :telephoneMobNumber " + "AND m.emailAddress = :emailAddress "
			+ "AND m.gstNumber = :gstNumber " + "AND m.nic2008ActivityCode = :nic2008ActivityCode")
	List<MstRegistryDetailsPageEntity> findAllByRequiredFieldsForDuplicateRecord(
			@Param("nameOfEstablishmentOrOwner") String nameOfEstablishmentOrOwner, @Param("houseNo") String houseNo,
			@Param("streetName") String streetName, @Param("locality") String locality,
			@Param("pinCode") Integer pinCode, @Param("panNumber") String panNumber,
			@Param("tanNumber") String tanNumber, @Param("telephoneMobNumber") Long telephoneMobNumber,
			@Param("emailAddress") String emailAddress, @Param("gstNumber") String gstNumber,
			@Param("nic2008ActivityCode") Integer nic2008ActivityCode);

	// @Query("SELECT e FROM MstRegistryDetailsPageEntity e WHERE
	// e.nameOfEstablishmentOrOwner = :nameOfEstablishmentOrOwner AND
	// e.telephoneMobNumber = :telephoneMobNumber AND e.emailAddress = :emailAddress
	// AND e.panNumber = :panNumber AND e.tanNumber = :tanNumber AND
	// e.nic2008ActivityCode = :nic2008ActivityCode AND e.gstNumber = :gstNumber AND
	// e.houseNo = :houseNo AND e.streetName = :streetName AND e.locality =
	// :locality AND e.townVillage = :townVillage AND e.taluka = :taluka AND
	// e.district = :district AND e.pinCode = :pinCode AND e.sector = :sector")
	@Query("SELECT e FROM MstRegistryDetailsPageEntity e WHERE "
			+ "(:nameOfEstablishmentOrOwner IS NULL OR e.nameOfEstablishmentOrOwner = :nameOfEstablishmentOrOwner) AND "
			+ "(:telephoneMobNumber IS NULL OR e.telephoneMobNumber = :telephoneMobNumber) AND "
			+ "(:emailAddress IS NULL OR e.emailAddress = :emailAddress) AND "
			+ "(:panNumber IS NULL OR e.panNumber = :panNumber) AND "
			+ "(:tanNumber IS NULL OR e.tanNumber = :tanNumber) AND "
			+ "(:nic2008ActivityCode IS NULL OR e.nic2008ActivityCode = :nic2008ActivityCode) AND "
			+ "(:gstNumber IS NULL OR e.gstNumber = :gstNumber) AND "
			+ "(:houseNo IS NULL OR e.houseNo = :houseNo) AND "
			+ "(:streetName IS NULL OR e.streetName = :streetName) AND "
			+ "(:locality IS NULL OR e.locality = :locality) AND "
			+ "(:townVillage IS NULL OR e.townVillage = :townVillage) AND "
			+ "(:taluka IS NULL OR e.taluka = :taluka) AND " + "(:district IS NULL OR e.district = :district) AND "
			+ "(:pinCode IS NULL OR e.pinCode = :pinCode) AND " + "(:sector IS NULL OR e.sector = :sector)")
	Optional<MstRegistryDetailsPageEntity> findByRegistryDetails(
			@Param("nameOfEstablishmentOrOwner") String nameOfEstablishmentOrOwner,
			@Param("telephoneMobNumber") Long telephoneMobNumber, @Param("emailAddress") String emailAddress,
			@Param("panNumber") String panNumber, @Param("tanNumber") String tanNumber,
			@Param("nic2008ActivityCode") Integer nic2008ActivityCode, @Param("gstNumber") String gstNumber,
			@Param("houseNo") String houseNo, @Param("streetName") String streetName,
			@Param("locality") String locality, @Param("townVillage") String townVillage,
			@Param("taluka") String taluka, @Param("district") String district, @Param("pinCode") Integer pinCode,
			@Param("sector") String sector);
	
	//search brnNo or nameOfEstablishmentOrOwner form the district
	 @Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.district = :district AND " +
	           "(m.brnNo = :brnNo OR m.nameOfEstablishmentOrOwner = :nameOfEstablishmentOrOwner)")
	    List<MstRegistryDetailsPageEntity> findByDistrictAndBrnNoOrNameOfEstablishmentOrOwner(
	        @Param("district") String district,
	        @Param("brnNo") String brnNo,
	        @Param("nameOfEstablishmentOrOwner") String nameOfEstablishmentOrOwner);
}
