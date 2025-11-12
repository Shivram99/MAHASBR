package com.mahasbr.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.SiteVisit;
import com.mahasbr.repository.SiteVisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteVisitService {

    private final SiteVisitRepository siteVisitRepository;

    public void recordVisit(String ipAddress, String userAgent) {
        SiteVisit visit = SiteVisit.builder()
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .visitedAt(LocalDateTime.now())
                .build();
        siteVisitRepository.save(visit);
    }

    public long getTotalVisits() {
        return siteVisitRepository.countTotalVisits();
    }

    public long getTodayVisits() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return siteVisitRepository.countTodayVisits(startOfDay, endOfDay);
    }
}
