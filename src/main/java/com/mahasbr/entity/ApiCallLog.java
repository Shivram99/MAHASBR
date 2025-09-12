package com.mahasbr.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCallLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_call_log_seq")
	@SequenceGenerator(name = "api_call_log_seq", sequenceName = "api_call_log_seq", allocationSize = 1)
	private Long id;

	@Column(name = "api_name", length = 100, nullable = false)
	private String apiName;

	@Column(name = "url", length = 500, nullable = false)
	private String url;

	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "response", length = 4000)
	private String response;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;
}
