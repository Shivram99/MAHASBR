package com.mahasbr.repository;
//src/main/java/com/example/repository/MstSubMenuRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.MstSubMenuEntity;

public interface MstSubMenuRepository extends JpaRepository<MstSubMenuEntity, Integer> {
 boolean existsBySubMenuCode(Integer subMenuCode); // To prevent duplicates
}
