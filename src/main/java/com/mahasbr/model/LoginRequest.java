package com.mahasbr.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LoginRequest {

	@NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Recaptcha is required")
    private String recaptchaResponse;
	
}
