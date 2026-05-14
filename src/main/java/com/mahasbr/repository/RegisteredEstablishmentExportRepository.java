package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.mahasbr.dto.RegisteredEstablishmentExportDto;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

public interface RegisteredEstablishmentExportRepository extends Repository<MstRegistryDetailsPageEntity, Long> {

	@Query("""
			SELECT new com.mahasbr.dto.RegisteredEstablishmentExportDto(
			    m.siNo,
			    m.brnNo,
			    m.nameOfEstablishmentOrOwner,
			    COALESCE(m.taluka, m.townVillage),
			    m.district,
			    m.sector
			)
			FROM MstRegistryDetailsPageEntity m
			WHERE (:applyRegistryFilter = false OR m.regUserId = :registryId)
			  AND (:applyDistrictFilter = false OR LOWER(m.district) IN :districts)
			  AND (:applyTalukaFilter = false OR LOWER(m.taluka) IN :talukas)
			  AND (:brn IS NULL OR m.brnNo = :brn)
			ORDER BY m.siNo ASC
			""")
	Slice<RegisteredEstablishmentExportDto> findForPdfExport(
			@Param("applyRegistryFilter") boolean applyRegistryFilter,
			@Param("registryId") Integer registryId,
			@Param("applyDistrictFilter") boolean applyDistrictFilter,
			@Param("districts") List<String> districts,
			@Param("applyTalukaFilter") boolean applyTalukaFilter,
			@Param("talukas") List<String> talukas,
			@Param("brn") String brn,
			Pageable pageable);
}
