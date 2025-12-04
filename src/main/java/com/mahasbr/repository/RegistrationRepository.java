package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.dto.CitizenDashboardData;
import com.mahasbr.dto.CitizenDashboardDataRegDeRegNewReg;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Repository
public interface RegistrationRepository extends JpaRepository<MstRegistryDetailsPageEntity, Long> {

	@Query(value = """
			SELECT
			    district,
			    EXTRACT(YEAR FROM registration_date) AS year,
			    'Q' || TO_CHAR(registration_date, 'Q') AS quarter,
			    COUNT(*) AS total_registrations
			FROM mst_reg_details
			WHERE registration_date IS NOT NULL
			GROUP BY district, EXTRACT(YEAR FROM registration_date), TO_CHAR(registration_date, 'Q')
			ORDER BY district, year, quarter
			""", nativeQuery = true)
	List<Object[]> getRegistrationStatsRaw();

	@Query(value = """
			             SELECT
			    mstreg.registry_name_en AS "REGISTRYNAME",
			    UPPER(mst.DISTRICT) AS "DISTRICT",
			    divi.division_name AS "DIVISION",
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE) AS "YEAR",
			    'Q' || TO_CHAR(mst.REGISTRATION_DATE, 'Q') AS "QUARTER",

			    -- 🎯 Count only current year registrations
			    SUM(
			        CASE
			            WHEN EXTRACT(YEAR FROM mst.REGISTRATION_DATE) = EXTRACT(YEAR FROM SYSDATE)
			            THEN 1 ELSE 0
			        END
			    ) AS "TOTALREGISTRATIONS",

			    -- 🎯 Sum working persons only for active records (no duplicates)
			    SUM(NVL(mst.TOTAL_PERSONS_WORKING, 0)) AS "TOTALPERSONSWORKING"

			FROM MST_REG_DETAILS mst
			INNER JOIN MST_REGISTRY_MASTER mstreg
			    ON mstreg.ID = mst.REG_USER_ID
			INNER JOIN district_master dist
			    ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
			INNER JOIN division_master divi
			    ON UPPER(divi.division_code) = UPPER(dist.division_code)

			-- 📌 Last 12 months filter
			WHERE mst.REGISTRATION_DATE >= ADD_MONTHS(TRUNC(SYSDATE), -12)
			  AND mst.REGISTRATION_DATE < TRUNC(SYSDATE)
			  AND mst.REGISTRATION_DATE IS NOT NULL

			GROUP BY
			    mstreg.registry_name_en,
			    mst.DISTRICT,
			    divi.division_name,
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
			    TO_CHAR(mst.REGISTRATION_DATE, 'Q')

			ORDER BY
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE) DESC,
			    CAST(TO_CHAR(mst.REGISTRATION_DATE, 'Q') AS NUMBER),
			    divi.division_name,
			    mst.DISTRICT,
			    mstreg.registry_name_en
			            """, nativeQuery = true)
	List<CitizenDashboardData> citizenDashboardDataNR();

	@Query(value = """
			SELECT
			    mstreg.registry_name_en AS "REGISTRYNAME",
			    mst.DISTRICT AS "DISTRICT",
			    divi.division_name AS "DIVISION",
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE) AS "YEAR",
			    'Q' || TO_CHAR(mst.REGISTRATION_DATE, 'Q') AS "QUARTER",
			    COUNT(*) AS "TOTALREGISTRATIONS",
			    SUM(NVL(mst.TOTAL_PERSONS_WORKING, 0)) AS "TOTALPERSONSWORKING"
			FROM MST_REG_DETAILS mst
			INNER JOIN MST_REGISTRY_MASTER mstreg
			    ON mstreg.ID = mst.REG_USER_ID
			INNER JOIN district_master dist
			    ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
			INNER JOIN division_master divi
			    ON UPPER(divi.division_Code) = UPPER(dist.division_code)
			WHERE mst.REGISTRATION_DATE IS NOT NULL
			  AND EXTRACT(YEAR FROM mst.REGISTRATION_DATE) >= 2010
			  AND TRUNC(mst.REGISTRATION_DATE) <= TRUNC(SYSDATE)
			GROUP BY
			    mstreg.registry_name_en,
			    mst.DISTRICT,
			    divi.division_name,
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
			    TO_CHAR(mst.REGISTRATION_DATE, 'Q')
			ORDER BY
			    EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
			    "QUARTER",
			    "DIVISION",
			    "DISTRICT",
			    "REGISTRYNAME"
			""", nativeQuery = true)
	List<CitizenDashboardData> citizenDashboardDataTR();

	@Query(value = """
			        SELECT
			           mstreg.registry_name_en AS registryName,
			           mst.DISTRICT AS district,
			           divi.division_name AS divisionName,
			           EXTRACT(YEAR FROM mst.deregistration_expiry_date) AS year,
			           'Q' || TO_CHAR(mst.deregistration_expiry_date, 'Q') AS quarter,
			           COUNT(*) AS totalRegistrations
			       FROM MST_REG_DETAILS mst
			       INNER JOIN MST_REGISTRY_MASTER mstreg
			           ON mstreg.ID = mst.REG_USER_ID
			       INNER JOIN district_master dist
			           ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
			       INNER JOIN division_master divi
			           ON UPPER(divi.division_Code) = UPPER(dist.division_code)
			       WHERE mst.deregistration_expiry_date IS NOT NULL
			         AND mst.deregistration_expiry_date < SYSDATE
			AND mst.deregistration_expiry_date >= ADD_MONTHS(SYSDATE, -12)
			       GROUP BY
			           mstreg.registry_name_en,
			           mst.DISTRICT,
			           divi.division_name,
			           EXTRACT(YEAR FROM mst.deregistration_expiry_date),
			           TO_CHAR(mst.deregistration_expiry_date, 'Q')
			       ORDER BY
			           mstreg.registry_name_en,
			           divi.division_name,
			           mst.DISTRICT,
			           EXTRACT(YEAR FROM mst.deregistration_expiry_date),
			           TO_CHAR(mst.deregistration_expiry_date, 'Q')
			       """, nativeQuery = true)
	List<CitizenDashboardData> citizenDashboardDataDR();

	@Query(value = """
			           WITH REG_DATA AS (
			    -- Registrations (ALL years)
			    SELECT
			        mstreg.registry_name_en AS registryName,
			        UPPER(mst.DISTRICT) AS district,
			        divi.division_name AS division,
			        EXTRACT(YEAR FROM mst.REGISTRATION_DATE) AS year,
			        TO_CHAR(mst.REGISTRATION_DATE, '"Q"Q') AS quarter,
			        1 AS totalRegistrations,
			        NVL(mst.TOTAL_PERSONS_WORKING, 0) AS totalPersonsWorking,
			        0 AS totalDeregistrations,
			        CASE
			            WHEN EXTRACT(YEAR FROM mst.REGISTRATION_DATE) = EXTRACT(YEAR FROM SYSDATE)
			            THEN 1 ELSE 0
			        END AS newRegistrationsThisYear
			    FROM MST_REG_DETAILS mst
			    JOIN MST_REGISTRY_MASTER mstreg ON mstreg.ID = mst.REG_USER_ID
			    JOIN district_master dist ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
			    JOIN division_master divi ON UPPER(divi.division_code) = UPPER(dist.division_code)
			    WHERE mst.REGISTRATION_DATE IS NOT NULL

			    UNION ALL

			    -- Deregistrations (ALL years)
			    SELECT
			        mstreg.registry_name_en AS registryName,
			        UPPER(mst.DISTRICT) AS district,
			        divi.division_name AS division,
			        EXTRACT(YEAR FROM mst.deregistration_expiry_date) AS year,
			        TO_CHAR(mst.deregistration_expiry_date, '"Q"Q') AS quarter,
			        0 AS totalRegistrations,
			        0 AS totalPersonsWorking, -- 👈 No contribution from dereg rows
			        1 AS totalDeregistrations,
			        0 AS newRegistrationsThisYear
			    FROM MST_REG_DETAILS mst
			    JOIN MST_REGISTRY_MASTER mstreg ON mstreg.ID = mst.REG_USER_ID
			    JOIN district_master dist ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
			    JOIN division_master divi ON UPPER(divi.division_code) = UPPER(dist.division_code)
			    WHERE mst.deregistration_expiry_date IS NOT NULL
			)

			SELECT
			    r.registryName,
			    r.district,
			    r.division,
			    r.year,
			    r.quarter,
			    NVL(SUM(r.totalRegistrations),0) AS totalRegistrations,
			    NVL(SUM(r.totalPersonsWorking),0) AS totalPersonsWorking,  -- 👈 MATCHES YOUR BASELINE QUERY
			    NVL(SUM(r.totalDeregistrations),0) AS totalDeregistrations,
			    NVL(SUM(r.newRegistrationsThisYear),0) AS newRegistrationsThisYear
			FROM REG_DATA r
			GROUP BY
			    r.registryName,
			    r.district,
			    r.division,
			    r.year,
			    r.quarter
			ORDER BY
			    r.year DESC,
			    CAST(SUBSTR(r.quarter, 2) AS NUMBER),
			    r.division,
			    r.district,
			    r.registryName
			            """, nativeQuery = true)
	List<CitizenDashboardDataRegDeRegNewReg> citizenDashboardDataRegDeRegNewReg();

}
