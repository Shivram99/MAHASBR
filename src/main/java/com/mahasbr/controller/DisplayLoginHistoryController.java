package com.mahasbr.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

@RestController
@RequestMapping("/admin")
public class DisplayLoginHistoryController {

	@Autowired
	private AuditLogRepository auditLogRepository;

	
	@GetMapping("/getAllAuditLogs")
	public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
	    List<AuditLog> auditLogs = auditLogRepository.findAll();
       
	    return ResponseEntity.ok(auditLogs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
	}
	
	

    private AuditLog convertToDto(AuditLog auditLog) {
    	
    	
        return new AuditLog(
                auditLog.getId(),
                auditLog.getUsername(),
                auditLog.getAction(),
                auditLog.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yy h:mm:ss.SSSSSSSSS a")),
                auditLog.getIpAddress(),
                auditLog.getStatus()
        );
    }

}
