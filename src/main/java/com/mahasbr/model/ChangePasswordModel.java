package com.mahasbr.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordModel {

	/** Must stay in sync with PASSWORD_PATTERN in the frontend change-password component. */
	public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,64}$";
	public static final String PASSWORD_RULE_MESSAGE =
			"Use 8-64 characters with an uppercase letter, a lowercase letter, a number and a symbol.";

	@NotBlank(message = "New password is required.")
	@Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_RULE_MESSAGE)
	private String newPassword;

	@NotBlank(message = "Please confirm the new password.")
	private String newPasswordConfirm;

	@NotBlank(message = "Current password is required.")
	private String password;

	// isFirstTimeLogin was removed on purpose: the server decides it, never the client.
}
