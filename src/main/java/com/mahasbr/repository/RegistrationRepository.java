package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.dto.CitizenDashboardData;
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
                mstreg.registry_name_en AS registryName,
                mst.DISTRICT AS district,
                divi.division_name AS divisionName,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE) AS year,
                'Q' || TO_CHAR(mst.REGISTRATION_DATE, 'Q') AS quarter,
                COUNT(*) AS totalRegistrations
            FROM MST_REG_DETAILS mst
            INNER JOIN MST_REGISTRY_MASTER mstreg 
                ON mstreg.ID = mst.REG_USER_ID
            INNER JOIN district_master dist 
                ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
            INNER JOIN division_master divi 
                ON UPPER(divi.division_Code) = UPPER(dist.division_code)
            WHERE mst.REGISTRATION_DATE IS NOT NULL
              AND mst.REGISTRATION_DATE >= ADD_MONTHS(SYSDATE, -12)
            GROUP BY 
                mstreg.registry_name_en,
                mst.DISTRICT,
                divi.division_name,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
                TO_CHAR(mst.REGISTRATION_DATE, 'Q')
            ORDER BY 
                mstreg.registry_name_en,
                divi.division_name,
                mst.DISTRICT,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
                TO_CHAR(mst.REGISTRATION_DATE, 'Q')
            """, nativeQuery = true)
        List<CitizenDashboardData> citizenDashboardDataNR();
    
    @Query(value = """
            SELECT 
                mstreg.registry_name_en AS registryName,
                mst.DISTRICT AS district,
                divi.division_name AS divisionName,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE) AS year,
                'Q' || TO_CHAR(mst.REGISTRATION_DATE, 'Q') AS quarter,
                COUNT(*) AS totalRegistrations
            FROM MST_REG_DETAILS mst
            INNER JOIN MST_REGISTRY_MASTER mstreg 
                ON mstreg.ID = mst.REG_USER_ID
            INNER JOIN district_master dist 
                ON UPPER(dist.district_name) = UPPER(mst.DISTRICT)
            INNER JOIN division_master divi 
                ON UPPER(divi.division_Code) = UPPER(dist.division_code)
            WHERE mst.REGISTRATION_DATE IS NOT NULL
            GROUP BY 
                mstreg.registry_name_en,
                mst.DISTRICT,
                divi.division_name,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
                TO_CHAR(mst.REGISTRATION_DATE, 'Q')
            ORDER BY 
                mstreg.registry_name_en,
                divi.division_name,
                mst.DISTRICT,
                EXTRACT(YEAR FROM mst.REGISTRATION_DATE),
                TO_CHAR(mst.REGISTRATION_DATE, 'Q')
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
}
