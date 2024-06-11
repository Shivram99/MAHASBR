package com.mahasbr.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.MstMenu;

@Repository
public interface MstMenuRepository extends JpaRepository<MstMenu, Long> {
}