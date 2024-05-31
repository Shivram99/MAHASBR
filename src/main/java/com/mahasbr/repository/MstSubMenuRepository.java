package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.MstSubMenu;

@Repository
public interface MstSubMenuRepository extends JpaRepository<MstSubMenu, Long> {

}
