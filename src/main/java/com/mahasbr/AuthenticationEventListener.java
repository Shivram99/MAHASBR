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
    
//    @EventListener
//    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        Authentication authentication = event.getAuthentication();
//        String username = extractUsername(authentication);
//        HttpServletRequest request = getRequest();
//        String ip = request.getRemoteAddr();
//        String jwtId = extractJwtId(request);
//
//        AuditLog log = new AuditLog();
//        log.setUsername(username);
//        log.setJwtId(jwtId != null ? jwtId : UUID.randomUUID().toString());
//        log.setIpAddress(ip);
//        log.setTimestamp(LocalDateTime.now());
//        log.setHttpMethod(request.getMethod());
//        log.setUrl(request.getRequestURI());
//        log.setSuccess(true);
//        log.setMessage("User login successful");
//        log.setResponseStatus(200);
//        log.setUserAgent(request.getHeader("User-Agent"));
//        log.setReferrer(request.getHeader("referer"));
//        log.setProcessId(Thread.currentThread().getName());
//
//        auditLogRepository.save(log);
//    }

//    @EventListener
//    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
//        String username = event.getAuthentication().getName() != null ? event.getAuthentication().getName() : "Anonymous";
//        HttpServletRequest request = getRequest();
//        String ip = request.getRemoteAddr();
//        String jwtId = extractJwtId(request);
//
//        AuditLog log = new AuditLog();
//        log.setUsername(username);
//        log.setJwtId(jwtId != null ? jwtId : UUID.randomUUID().toString());
//        log.setIpAddress(ip);
//        log.setTimestamp(LocalDateTime.now());
//        log.setHttpMethod(request.getMethod());
//        log.setUrl(request.getRequestURI());
//        log.setSuccess(false);
//        log.setMessage("User login failed");
//        log.setResponseStatus(401);
//        log.setUserAgent(request.getHeader("User-Agent"));
//        log.setReferrer(request.getHeader("referer"));
//        log.setProcessId(Thread.currentThread().getName());
//
//        auditLogRepository.save(log);
//    }
//
//    @EventListener
//    public void handleLogout(LogoutSuccessEvent event) {
//        Authentication authentication = event.getAuthentication();
//        String username = extractUsername(authentication);
//        HttpServletRequest request = getRequest();
//        String ip = request.getRemoteAddr();
//        String jwtId = extractJwtId(request);
//
//        AuditLog log = new AuditLog();
//        log.setUsername(username);
//        log.setJwtId(jwtId != null ? jwtId : UUID.randomUUID().toString());
//        log.setIpAddress(ip);
//        log.setTimestamp(LocalDateTime.now());
//        log.setHttpMethod(request.getMethod());
//        log.setUrl(request.getRequestURI());
//        log.setSuccess(true);
//        log.setMessage("User logout successful");
//        log.setResponseStatus(200);
//        log.setUserAgent(request.getHeader("User-Agent"));
//        log.setReferrer(request.getHeader("referer"));
//        log.setProcessId(Thread.currentThread().getName());
//
//        auditLogRepository.save(log);
//    }

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
