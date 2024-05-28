package com.mahasbr.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class ChangePasswordModel {
	
	@NotEmpty
	private String newPassword;
	@NotEmpty
	private String newPasswordConfirm;
	
	@NotEmpty
	private String password;

    

}
