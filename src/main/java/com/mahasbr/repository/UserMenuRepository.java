package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahasbr.entity.UserMenuEntity;

public interface UserMenuRepository extends JpaRepository<UserMenuEntity, Long> {
    List<UserMenuEntity> findByUser_Id(Long userId);
    void deleteByUser_IdAndMenu_Id(Long userId, Long menuId);
}
