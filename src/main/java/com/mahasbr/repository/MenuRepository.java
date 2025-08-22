package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
	List<Menu> findDistinctByRoles_Id(Long roleId);

	@Query("SELECT DISTINCT m FROM Menu m JOIN m.roles r WHERE r.id = :roleId")
	List<Menu> findDistinctByRoleId(@Param("roleId") Long roleId);
}