package com.mahasbr;

import java.time.LocalDateTime;
import java.util.UUID;

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
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthenticationEventListener {
 

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    // Utility Methods
    private String extractUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return "Anonymous";
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        else if (principal instanceof String) return (String) principal;
        return "Anonymous";
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
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
