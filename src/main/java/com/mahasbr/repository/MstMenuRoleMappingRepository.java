package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.MstMenuRoleMapping;

@Repository
public interface MstMenuRoleMappingRepository extends JpaRepository<MstMenuRoleMapping, Long> {

}
