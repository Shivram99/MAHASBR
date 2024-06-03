package com.mahasbr;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

@Component
public class AuthenticationEventListener {

	@Autowired
    private  AuditLogRepository auditLogRepository;


    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String ipAddress = getIpAddress();
        auditLogRepository.save(new AuditLog(username, "LOGIN_SUCCESS", LocalDateTime.now(), ipAddress));
    }

    @EventListener
    public void handleLogoutSuccess(LogoutSuccessEvent event) {
    	 String username = event.getAuthentication().getName();
        String ipAddress = getIpAddress();
        auditLogRepository.save(new AuditLog(username, "LOGOUT_SUCCESS", LocalDateTime.now(), ipAddress));
    }

    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    	 String username = event.getAuthentication().getName();
        String ipAddress = getIpAddress();
        auditLogRepository.save(new AuditLog(username, "LOGIN_FAILURE", LocalDateTime.now(), ipAddress));
    }
  
    private String getIpAddress() {
    	 ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return  attributes.getRequest().getRemoteAddr();
    }
}
