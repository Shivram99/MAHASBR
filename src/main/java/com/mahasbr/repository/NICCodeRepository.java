package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mahasbr.entity.NICCodeEntity;

public interface NICCodeRepository extends JpaRepository<NICCodeEntity, Long> {
    // Additional query methods (if needed) can be defined here
	@Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NICCodeEntity n WHERE n.code = :code")
		boolean existsByCode(@Param("code") String code);

}
