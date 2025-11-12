package com.mahasbr.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.service.SiteVisitService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/citizenSearch/visitor")
@RequiredArgsConstructor
public class VisitorController {

	  private final SiteVisitService siteVisitService;

	    @PostMapping
	    public Map<String, Object> recordVisit(HttpServletRequest request) {
	        String ip = request.getRemoteAddr();
	        String userAgent = request.getHeader("User-Agent");

	        siteVisitService.recordVisit(ip, userAgent);

	        return Map.of("message", "Visit recorded successfully");
	    }

	    @GetMapping("/summary")
	    public Map<String, Object> getVisitSummary() {
	        return Map.of(
	                "totalVisits", siteVisitService.getTotalVisits(),
	                "todayVisits", siteVisitService.getTodayVisits()
	        );
	    }
}
