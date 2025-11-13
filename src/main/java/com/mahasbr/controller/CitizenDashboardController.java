package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RegistrationStatsDTO;
import com.mahasbr.service.RegistrationService;

@RestController
@RequestMapping("/citizenSearch")
public class CitizenDashboardController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/registrationStats")
    public ResponseEntity<List<RegistrationStatsDTO>> getRegistrationStats() {
        List<RegistrationStatsDTO> stats = registrationService.getRegistrationStats();
        System.out.println(stats);
        return ResponseEntity.ok(stats);
    }
}
