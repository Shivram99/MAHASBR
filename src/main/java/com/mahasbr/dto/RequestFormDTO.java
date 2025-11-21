package com.mahasbr.dto;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class RequestFormDTO {

	    @NotBlank(message = "Name is required")
	    @Size(max = 100)
	    private String name;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String email;

	    @Size(max = 15)
	    private String mobile;

	    private String district;
	    
	    @NotBlank(message = "Reason is required")
	    private String reason;

	    @NotBlank(message = "Message is required")
	    @Size(max = 5000, message = "Message must be under 5000 characters")
	    private String message;

}
