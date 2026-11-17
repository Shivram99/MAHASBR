package com.mahasbr.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mahasbr.config.CredentialEncryptionConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One external registry API that MAHASBR pulls establishment data from.
 * Only rows with {@code active = true} are called, by the nightly job or by "Run now".
 */
@Entity
@Table(name = "registry_api_auth_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryApiAuthConfigEntity extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "service_name")
	private String serviceName;

	@Column(name = "service_name_code")
	private String serviceNameCode;

	@Column(name = "auth_url")
	private String authUrl;

	@Column(name = "auth_url_Method")
	private String authUrlMethod;

	private String username;

	/** Encrypted at rest when REGISTRY_API_CREDENTIAL_KEY is set. Never sent to the browser. */
	@Convert(converter = CredentialEncryptionConverter.class)
	@Column(name = "password", length = 512)
	private String password;

	@Column(name = "api_Data")
	private String apiData;

	@Column(name = "api_Data_Method")
	private String apiDataMethod;

	@Column(name = "active")
	private Boolean active = true;

	/** The registry this data belongs to; its id is stored as reg_user_id on every record. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "registry_id")
	private RegistryMasterEntity registry;

	/** First date fetched on the very first sync. */
	@Column(name = "sync_from_date")
	private LocalDate syncFromDate;

	/** Last date fetched successfully; the next sync continues from the day after. */
	@Column(name = "last_synced_to")
	private LocalDate lastSyncedTo;

	@Column(name = "last_run_at")
	private LocalDateTime lastRunAt;

	/** RUNNING, SUCCESS or FAILED. */
	@Column(name = "last_run_status", length = 20)
	private String lastRunStatus;

	@Column(name = "last_run_message", length = 1000)
	private String lastRunMessage;
}
