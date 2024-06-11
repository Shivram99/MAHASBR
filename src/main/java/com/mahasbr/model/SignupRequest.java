package com.mahasbr.model;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class SignupRequest {
	
	
	Long id;

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	private Set<String> role;
	
	
	 private String roles;
	 
	 
	 private Long departmentId;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 12)
	@Email
	private String phoneNo;

	@NotBlank
	@Size(min = 6, max = 40)
	private String password;

	
	
	
	

}