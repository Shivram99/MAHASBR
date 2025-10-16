package com.mahasbr.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq_generator")
	@SequenceGenerator(name = "audit_seq_generator", sequenceName = "audit_seq", allocationSize = 1)
	private Long id;

    private String username;
    
    private String ipAddress;

    private String jwtId;
    
    private String url;

    private String httpMethod;

    private String referrer;
    
    private String userAgent;
    
    private boolean success;
    
    private String message;

    private LocalDateTime timestamp;
    
    private String country;
    
    private Integer responseStatus;

    private String processId;

    private String requestHash;
    
    private String responseHash;
    

    public void AuditLogDto(Long id, String username, String message, LocalDateTime timestamp, String ipAddress, Boolean success) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
        this.success = success;
    }

}
