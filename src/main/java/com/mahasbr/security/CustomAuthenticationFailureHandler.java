package com.mahasbr.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
		auditLogRepository.save(new AuditLog(username, "LOGIN_FAILED", LocalDateTime.now(), ipAddress));
		
		
		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

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
