package com.mahasbr.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

	private final AuditLogRepository auditLogRepository;

	private final JwtUtils jwtUtils;

	public void logEvent(String username, String action, boolean success, HttpServletRequest request, String message,
			Integer responseStatus, String method) {
		String jwtId = extractJwtId(request);
		AuditLog log = new AuditLog();
		log.setUsername(username != null ? username : "Anonymous");
		log.setMessage(message != null ? message : action);
		log.setSuccess(success);
		log.setTimestamp(LocalDateTime.now());
		log.setIpAddress(request.getRemoteAddr());
		log.setUserAgent(request.getHeader("User-Agent"));
		log.setReferrer(request.getHeader("Referer"));
		log.setProcessId(UUID.randomUUID().toString());
		log.setUrl(request.getRequestURI());
		log.setResponseStatus(responseStatus);
		log.setHttpMethod(method);
		log.setJwtId(jwtId != null ? jwtId : UUID.randomUUID().toString());
		auditLogRepository.save(log);
	}

	private String extractJwtId(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			return jwtUtils.getJtiFromJwtToken(token); // implement this in JwtUtils
		}
		return null;
	}
}
