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

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String action;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	@Column(nullable = false)
	private String ipAddress;

	@Column(nullable = false)
	private String status;

	@Transient
	private String format;

	public AuditLog(String username, String action, LocalDateTime timestamp, String ipAddress) {
		this.username = username;
		this.action = action;
		this.timestamp = timestamp;
		this.ipAddress = ipAddress;
		this.status = action;
	}

	public AuditLog(Long id2, String username2, String action2, String format, String ipAddress2, String status2) {
		this.id = id2;
		this.username = username2;
		this.action = action2;
		this.format = format;
		this.ipAddress = ipAddress2;
		this.status = status2;
	}
}
