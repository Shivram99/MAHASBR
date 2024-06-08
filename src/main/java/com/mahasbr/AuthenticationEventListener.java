package com.mahasbr;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mahasbr.entity.AuditLog;
import com.mahasbr.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthenticationEventListener {
 

    @Autowired
    private AuditLogRepository auditLogRepository;

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = "Anonymous"; // Default username if not available
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        String ipAddress = getClientIpAddress();
        auditLogRepository.save(new AuditLog(username, "LOGIN_SUCCESS", LocalDateTime.now(), ipAddress));
        
        
    }

    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        String ipAddress = getClientIpAddress();
        auditLogRepository.save(new AuditLog(username != null ? username : "Anonymous", "LOGIN_FAILURE", LocalDateTime.now(), ipAddress));
    }

    @EventListener
    public void handleLogout(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String ipAddress = getClientIpAddress();
        auditLogRepository.save(new AuditLog(username != null ? username : "Anonymous", "LOGOUT", LocalDateTime.now(), ipAddress));
    }

    private String getClientIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRemoteAddr();
    }
}
