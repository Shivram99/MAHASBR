package com.mahasbr.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registry_api_auth_config")
@Getter @Setter
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
	
	private String password;	
	
	@Column(name = "api_Data")
	private String apiData;
	
	@Column(name = "api_Data_Method")
	private String apiDataMethod;
		
	@Column(name = "active") 
    private Boolean active = true;	
}
