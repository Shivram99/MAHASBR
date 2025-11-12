package com.mahasbr.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mahasbr.entity.SiteVisit;

public interface SiteVisitRepository extends JpaRepository<SiteVisit, Long> {

    @Query("SELECT COUNT(v) FROM SiteVisit v")
    long countTotalVisits();

    @Query("SELECT COUNT(v) FROM SiteVisit v WHERE v.visitedAt BETWEEN :start AND :end")
    long countTodayVisits(LocalDateTime start, LocalDateTime end);
}