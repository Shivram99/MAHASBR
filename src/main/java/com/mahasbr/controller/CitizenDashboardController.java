package com.mahasbr.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.CitizenDashboardData;
import com.mahasbr.dto.CitizenDashboardFilter;
import com.mahasbr.dto.RegistrationStatsDTO;
import com.mahasbr.service.ApiSchedulerService;
import com.mahasbr.service.RegistrationService;

@RestController
@RequestMapping("/citizenSearch")
public class CitizenDashboardController {

	private static final Logger logger = LoggerFactory.getLogger(CitizenDashboardController.class);
	
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private  ObjectMapper objectMapper;
    
    @Autowired
    private ApiSchedulerService apiSchedulerService;

    @GetMapping("/registrationStats")
    public ResponseEntity<List<RegistrationStatsDTO>> getRegistrationStats() {
        List<RegistrationStatsDTO> stats = registrationService.getRegistrationStats();
        System.out.println(stats);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/dashboardData")
    public ResponseEntity<List<CitizenDashboardData>> getDashboardData(@RequestBody(required = false) CitizenDashboardFilter filter) {
        if (filter == null) {
            logger.warn("⚠️ No request body received!");
            return ResponseEntity.badRequest().build();
        }
        List<CitizenDashboardData> citizenDashboardData = new ArrayList<>();
        logger.info("📩 Received Filter: {}", filter);

        if ("NR".equals(filter.getCountType())) {
            citizenDashboardData = registrationService.citizenDashboardDataNR();
        }else if ("DR".equals(filter.getCountType())) {
            citizenDashboardData = registrationService.citizenDashboardDataDR();
        } else {
        	citizenDashboardData = registrationService.citizenDashboardDataTR();
        }
//        try {
//            logger.info("Citizen Dashboard Data: {}", 
//                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(citizenDashboardData));
//        } catch (JsonProcessingException e) {
//            logger.error("Error converting dashboard data to JSON", e);
//        }

        return ResponseEntity.ok(citizenDashboardData);
    }
    
    @GetMapping("/fetchGovApis")
    public ResponseEntity<String> fetchGovApis() {
    	apiSchedulerService.fetchGovApis();
		return ResponseEntity.ok("Success");
	}

}
