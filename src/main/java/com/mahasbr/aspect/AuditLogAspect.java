package com.mahasbr.aspect;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mahasbr.config.Audit;
import com.mahasbr.service.AuditLogService;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

//@Aspect
//@Component
//@RequiredArgsConstructor
public class AuditLogAspect {

//    private final AuditLogService auditLogService;
//    private final HttpServletRequest request;
//    
//    private final  JwtUtils jwtUtils;
//
//
//    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
//    public void logSuccess(JoinPoint joinPoint, Audit audit, Object result) {
//        String username = getCurrentUsername();
//        String jwtId = getCurrentJwtId();
//        String responseHash = result != null ? Integer.toString(result.hashCode()) : null;
//
//        auditLogService.logEvent(
//                request,
//                username,
//                jwtId,
//                true,
//                "Action executed successfully",
//                200,
//                null,
//                responseHash
//        );
//    }
//
//    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
//    public void logFailure(JoinPoint joinPoint, Audit audit, Throwable ex) {
//        String username = getCurrentUsername();
//        String jwtId = getCurrentJwtId();
//
//        auditLogService.logEvent(
//                request,
//                username,
//                jwtId,
//                false,
//                "Action failed: " + ex.getMessage(),
//                500,
//                null,
//                null
//        );
//    }
//
//    private String getCurrentUsername() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
//            return auth.getName();
//        }
//        return "Anonymous";
//    }
//
//    private String getCurrentJwtId() {
//        String token = request.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            return jwtUtils.getJtiFromJwtToken(token.substring(7));
//        }
//        return UUID.randomUUID().toString();
//    }
}

