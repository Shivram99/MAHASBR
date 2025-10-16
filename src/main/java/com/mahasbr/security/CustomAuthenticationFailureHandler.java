package com.mahasbr.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private AuditLogRepository auditLogRepository;

	private final ObjectMapper objectMapper;

	public CustomAuthenticationFailureHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (authentication != null) ? authentication.getName() : "UNKNOWN";
		String ipAddress = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");
		String referrer = request.getHeader("Referer");
		String processId = UUID.randomUUID().toString(); // Optional: unique process id
		String requestHash = ""; // Optional: hash of request payload if needed
		String responseHash = ""; // Optional: hash of response payload if needed
		String country = ""; // Optional: resolve from IP if required
		Integer responseStatus = HttpServletResponse.SC_UNAUTHORIZED;
		Boolean success = false;

		// Optional JWT info if available
		String jwtId = null;
		Long userId = null;

		AuditLog auditLog = new AuditLog();
		auditLog.setUsername(username);
		auditLog.setMessage("LOGIN_FAILED");
		auditLog.setTimestamp(LocalDateTime.now());
		auditLog.setIpAddress(ipAddress);
		auditLog.setUserAgent(userAgent);
		auditLog.setReferrer(referrer);
		auditLog.setProcessId(processId);
		auditLog.setRequestHash(requestHash);
		auditLog.setResponseHash(responseHash);
		auditLog.setCountry(country);
		auditLog.setResponseStatus(responseStatus);
		auditLog.setSuccess(success);
		auditLog.setJwtId(jwtId);

		// Save to DB
		auditLogRepository.save(auditLog);

		// Create a response object with the error message
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Authentication failed");
		errorResponse.put("message", exception.getMessage());

		// Convert the response object to JSON
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);

		// Write the JSON response to the response body
		response.getWriter().write(jsonResponse);
		response.getWriter().flush();
	}

}
