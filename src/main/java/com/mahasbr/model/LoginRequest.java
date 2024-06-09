package com.mahasbr.model;

import lombok.Data;

@Data
public class LoginRequest {

	String username;
	String password;

	private String recaptchaResponse;
	
}
