package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Repository
@EnableJpaRepositories
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

	@Query("FROM DetailsPage t WHERE UPPER(t.nameOfEstateOwner) = UPPER(:nameOfEstateOwner) AND "
			+ "UPPER(t.houseNo) = UPPER(:houseNo) AND UPPER(t.streetName) = UPPER(:streetName) AND "
			+ "UPPER(t.locality) = UPPER(:locality) AND UPPER(t.townVillage) = UPPER(:townVillage) AND "
			+ "UPPER(t.taluka) = UPPER(:taluka) AND UPPER(t.district) = UPPER(:district) AND "
			+ "UPPER(t.sector) = UPPER(:sector) AND UPPER(t.nameofAuth) = UPPER(:nameofAuth) AND "
			+ "UPPER(t.nameofAct) = UPPER(:nameofAct)")
	DetailsPage getDetailsByColumn(@Param("nameOfEstateOwner") String nameOfEstateOwner,
			@Param("houseNo") String houseNo, @Param("streetName") String streetName,
			@Param("locality") String locality, @Param("townVillage") String townVillage,
			@Param("taluka") String taluka, @Param("district") String district, @Param("sector") String sector,
			@Param("nameofAuth") String nameofAuth, @Param("nameofAct") String nameofAct);

	@Query("SELECT MAX(CAST(d.dateOfReg AS DATE)) FROM DetailsPage d")
	String findMaxDate();

	@Query("SELECT r.district, r.nameOfEstateOwner, COUNT(r) AS count " + "FROM Details"
			+ "Page r "
			+ "WHERE r.dateOfReg = :maxDate " + "GROUP BY r.district, r.nameOfEstateOwner")
	List<MstRegistryDetailsPageEntity> getIndustryWiseStats(@Param("maxDate") String maxDate);

	@Query(value = "SELECT nameofAct, " + "EXTRACT(YEAR FROM " + "CASE "
			+ "   WHEN REGEXP_LIKE(dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN TO_DATE(dateOfReg, 'DD/MM/YY') "
			+ "   ELSE DATE '1900-01-01' + (TO_NUMBER(dateOfReg) - 2) " + "END) AS year, " + "COUNT(*) AS count "
			+ "FROM DetailsPage " + "GROUP BY nameofAct, EXTRACT(YEAR FROM " + "CASE "
			+ "   WHEN REGEXP_LIKE(dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN TO_DATE(dateOfReg, 'DD/MM/YY') "
			+ "   ELSE DATE '1900-01-01' + (TO_NUMBER(dateOfReg) - 2) " + "END)", nativeQuery = true)
	List<MstRegistryDetailsPageEntity> findActAndYearCounts();

	@Query(value = "SELECT b.district AS district, " + "       b.nameofAct AS nameofAct, " + "       COUNT(*) AS count "
			+ "FROM DetailsPage b " + "WHERE REGEXP_LIKE(b.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') "
			+ "  AND TO_DATE(b.dateOfReg, 'DD/MM/YY') IS NOT NULL "
			+ "  AND EXTRACT(YEAR FROM TO_DATE(b.dateOfReg, 'DD/MM/YY')) = ("
			+ "      SELECT MAX(EXTRACT(YEAR FROM TO_DATE(r.dateOfReg, 'DD/MM/YY'))) " + "      FROM DetailsPage r "
			+ "      WHERE REGEXP_LIKE(r.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$')" + "  ) "
			+ "GROUP BY b.district, b.NAME_OF_ACT", nativeQuery = true)
	List<MstRegistryDetailsPageEntity> findDistrictAndActWiseForLatestYear();


    @Query(value = "SELECT " +
            "    CASE " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN " +
            "            CASE " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 1 AND 3 THEN 'Q1' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 4 AND 6 THEN 'Q2' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 7 AND 9 THEN 'Q3' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 10 AND 12 THEN 'Q4' " +
            "            END " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{5}$') THEN " +
            "            CASE " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 1 AND 3 THEN 'Q1' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 4 AND 6 THEN 'Q2' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 7 AND 9 THEN 'Q3' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 10 AND 12 THEN 'Q4' " +
            "            END " +
            "    END AS quarter, " +
            "    r.nameOfEstateOwner, " +
            "    COUNT(*) AS count " +
            "FROM details_page r " + // Ensure the table name matches exactly
            "WHERE EXTRACT(YEAR FROM " +
            "    CASE " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN TO_DATE(r.dateOfReg, 'DD/MM/YY') " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{5}$') THEN DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2) " +
            "    END) = ( " +
            "    SELECT MAX(EXTRACT(YEAR FROM " +
            "        CASE " +
            "            WHEN REGEXP_LIKE(r2.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN TO_DATE(r2.dateOfReg, 'DD/MM/YY') " +
            "            WHEN REGEXP_LIKE(r2.dateOfReg, '^\\d{5}$') THEN DATE '1900-01-01' + (TO_NUMBER(r2.dateOfReg) - 2) " +
            "        END " +
            "    )) " +
            "    FROM details_page r2 " +
            ") " +
            "GROUP BY " +
            "    CASE " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{2}/\\d{2}/\\d{2}$') THEN " +
            "            CASE " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 1 AND 3 THEN 'Q1' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 4 AND 6 THEN 'Q2' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 7 AND 9 THEN 'Q3' " +
            "                WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 10 AND 12 THEN 'Q4' " +
            "            END " +
            "        WHEN REGEXP_LIKE(r.dateOfReg, '^\\d{5}$') THEN " +
            "            CASE " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 1 AND 3 THEN 'Q1' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 4 AND 6 THEN 'Q2' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 7 AND 9 THEN 'Q3' " +
            "                WHEN EXTRACT(MONTH FROM DATE '1900-01-01' + (TO_NUMBER(r.dateOfReg) - 2)) BETWEEN 10 AND 12 THEN 'Q4'" +
            "            END " +
            "    END, " +
            "    r.nameOfEstateOwner", nativeQuery = true)
	List<MstRegistryDetailsPageEntity> getTimeAndIndustryWise();

	@Query(value = "SELECT r.district, " + "CASE "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 1 AND 3 THEN 'Q1' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 4 AND 6 THEN 'Q2' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 7 AND 9 THEN 'Q3' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 10 AND 12 THEN 'Q4' "
			+ "END AS quarter, " + "COUNT(*) AS count " + "FROM DetailsPage r "
			+ "WHERE EXTRACT(YEAR FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) = ("
			+ "    SELECT MAX(EXTRACT(YEAR FROM TO_DATE(r2.dateOfReg, 'DD/MM/YY'))) " + "    FROM DetailsPage r2 "
			+ ") " + "GROUP BY r.district, " + "CASE "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 1 AND 3 THEN 'Q1' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 4 AND 6 THEN 'Q2' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 7 AND 9 THEN 'Q3' "
			+ "    WHEN EXTRACT(MONTH FROM TO_DATE(r.dateOfReg, 'DD/MM/YY')) BETWEEN 10 AND 12 THEN 'Q4'"
			+ "END", nativeQuery = true)
	List<MstRegistryDetailsPageEntity> getDistricrtAndTimeWise();

//	@Query("FROM DetailsPage t WHERE t.nameOfEstateOwner=?1 and t.houseNo=?2 AND t.streetName=?3 AND t.locality=?4  AND t.taluka=?5  AND t.district=?6  AND t.sector=?7 AND t.pincode=?8 AND t.nameofAuth=?9  AND t.nameofAct=?10")
//    DetailsPage getDetailsByColumn(String nameOfEstateOwner, String houseNo, String streetName, String locality,
//        String townVillage, String taluka, String district, int pincode, String sector, String nameofAuth,
//        String nameofAct);

}
