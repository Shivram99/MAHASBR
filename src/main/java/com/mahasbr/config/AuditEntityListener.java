package com.mahasbr.config;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mahasbr.entity.Auditable;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.servlet.http.HttpServletRequest;

public class AuditEntityListener {

    @PrePersist
    public void prePersist(Auditable auditable) {
        String clientIp = getClientIp();
        String userAgent = getUserAgent();

        auditable.setCreatedIp(clientIp);
        auditable.setUpdatedIp(clientIp);
        auditable.setCreatedUserAgent(userAgent);
        auditable.setUpdatedUserAgent(userAgent);
        auditable.setCreatedDateTime(new java.util.Date());
        auditable.setUpdatedDateTime(new java.util.Date());
    }

    @PreUpdate
    public void preUpdate(Auditable auditable) {
        String clientIp = getClientIp();
        String userAgent = getUserAgent();

        auditable.setUpdatedIp(clientIp);
        auditable.setUpdatedUserAgent(userAgent);
        auditable.setUpdatedDateTime(new java.util.Date());
    }

    private String getClientIp() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request =
                        ((ServletRequestAttributes) requestAttributes).getRequest();

                String ip = request.getHeader("X-Forwarded-For"); // if behind proxy/load balancer
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
        return "UNKNOWN";
    }

    private String getUserAgent() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request =
                        ((ServletRequestAttributes) requestAttributes).getRequest();

                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
        return "UNKNOWN";
    }
}

