package com.mahasbr.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Autowired
	private AuditLogRepository auditLogRepository;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	        throws IOException, ServletException {

	    String username = "UNKNOWN";
	    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	        username = ((UserDetails) authentication.getPrincipal()).getUsername();
	    }

	    String ipAddress = request.getRemoteAddr();
	    String userAgent = request.getHeader("User-Agent");
	    String referrer = request.getHeader("Referer");
	    String processId = UUID.randomUUID().toString(); // Unique per event
	    String requestHash = "";  
	    String responseHash = "";
	    String country = "";  
	    Integer responseStatus = HttpServletResponse.SC_OK; 
	    Boolean success = true;  

	    String jwtId = null;
	    Long userId = null;

	    AuditLog auditLog = new AuditLog();
	    auditLog.setUsername(username);
	    auditLog.setMessage("LOGOUT_SUCCESS"); 
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

	    auditLogRepository.save(auditLog);

	    // Return a JSON response instead of redirect (stateless apps best practice)
	    response.setStatus(HttpServletResponse.SC_OK);
	    response.setContentType("application/json");
	    response.getWriter().write("{\"message\": \"User logged out successfully\"}");
	    response.getWriter().flush();
	}

}