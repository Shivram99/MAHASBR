package com.mahasbr.controller;

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

		return ResponseEntity.ok(auditLogs);
	}

		

}
