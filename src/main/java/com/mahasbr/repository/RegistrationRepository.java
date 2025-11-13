package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
