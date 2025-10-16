package com.mahasbr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// search brnNo or nameOfEstablishmentOrOwner form the district
	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE LOWER(m.district) = LOWER(:district) AND "
			+ "(m.brnNo = :brnNo OR LOWER(m.nameOfEstablishmentOrOwner) = LOWER(:nameOfEstablishmentOrOwner))")
	List<MstRegistryDetailsPageEntity> findByDistrictAndBrnNoOrNameOfEstablishmentOrOwner(
			@Param("district") String district, @Param("brnNo") String brnNo,
			@Param("nameOfEstablishmentOrOwner") String nameOfEstablishmentOrOwner);

	Optional<MstRegistryDetailsPageEntity> findByBrnNo(String brnno);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.taluka IN :talukas AND m.district IN :district")
	Page<MstRegistryDetailsPageEntity> findByTalukaInAndDistrictIn(@Param("talukas") List<String> talukas,
			@Param("district") List<String> district, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.taluka IN :talukas AND m.district IN :districts AND m.regUserId = :regUserId")
	Page<MstRegistryDetailsPageEntity> findByTalukasAndDistrictsAndRegUserId(@Param("talukas") List<String> talukas,
			@Param("districts") List<String> districts, @Param("regUserId") Long regUserId, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE LOWER(m.district) IN :districtsLower AND m.regUserId = :regUserId")
	Page<MstRegistryDetailsPageEntity> findByDistrictsAndRegUserId(@Param("districtsLower") List<String> districts,
			@Param("regUserId") Long regUserId, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.brnNo = :brnNo ")
	// @Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.brnNo = :brn")
	Page<MstRegistryDetailsPageEntity> findAllByBrnNoAndRegUserId(@Param("brnNo") String brn,
			 Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE m.regUserId = :regUserId")
	Page<MstRegistryDetailsPageEntity> findAllByRegUserId(@Param("regUserId") Long regUserId, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE LOWER(m.district) IN :districts")
	Page<MstRegistryDetailsPageEntity> findByDistricts(@Param("districts") List<String> districts, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE LOWER(m.district) = LOWER(:districtName)")
	Page<MstRegistryDetailsPageEntity> findByDistrictName(@Param("districtName") String districtName, Pageable pageable);

	@Query("SELECT m FROM MstRegistryDetailsPageEntity m WHERE LOWER(m.taluka) IN :talukas AND LOWER(m.district) IN :districts ")
	Page<MstRegistryDetailsPageEntity> findByTalukasAndDistricts(@Param("talukas") List<String> talukas,
			@Param("districts") List<String> districts, Pageable pageable);

}
