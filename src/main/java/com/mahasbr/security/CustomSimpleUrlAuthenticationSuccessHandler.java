package com.mahasbr.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomSimpleUrlAuthenticationSuccessHandler 
//implements AuthenticationSuccessHandler
{

	@Autowired
	private AuditLogRepository auditLogRepository;

//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws IOException, ServletException {
//		String username = (authentication != null) ? authentication.getName() : "UNKNOWN";
//		String ipAddress = request.getRemoteAddr();
//		auditLogRepository.save(new AuditLog(username, "LOGIN_SUCCESS", LocalDateTime.now(), ipAddress));
//	}

}
