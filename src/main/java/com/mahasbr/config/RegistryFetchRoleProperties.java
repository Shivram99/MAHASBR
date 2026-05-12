package com.mahasbr.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "registry.roles")
@Data
public class RegistryFetchRoleProperties {

	/** STATE level access */
	private Set<String> state;

	/** REGION level access */
	private Set<String> region;

	/** DISTRICT / BLOCK level access */
	private Set<String> district;

	/** Registry Auth users (API / CSV) */
	private Set<String> regAuth;

	/** Any other role (fallback) */
	private Set<String> others;
}
