package com.mahasbr.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mahasbr.api.entity.ShopDataEntity;

public interface ShopDataRepository extends JpaRepository<ShopDataEntity, Long> {

	boolean existsBySrNo(Integer srNo);
	
	@Query("SELECT s.srNo FROM ShopDataEntity s")
	List<Integer> findAllSrNos();

}
