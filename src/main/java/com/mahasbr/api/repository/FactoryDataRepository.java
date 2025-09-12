package com.mahasbr.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mahasbr.api.entity.FactoryDataEntity;

public interface FactoryDataRepository extends JpaRepository<FactoryDataEntity, Long> {

	boolean existsBySrNo(Integer srNo);
	
	@Query("SELECT f.srNo FROM FactoryDataEntity f")
	List<Integer> findAllSrNos();


}
