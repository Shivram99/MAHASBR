package com.mahasbr.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.User;
import com.mahasbr.model.ChangePasswordModel;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.ChangePasswordServceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class ChangePasswordController {
	private static final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);

	/** Old shared default password: existing accounts may still have it, so never accept it as a new one. */
	private static final String LEGACY_DEFAULT_PASSWORD = "Pass@123";

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private ChangePasswordServceImpl changePasswordServiceImpl;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/changePassword")
	public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordModel request,
			BindingResult bindingResult) {

		// BindingResult right after the @Valid argument means Spring hands us the errors instead of
		// throwing, so every failure below comes back in the same { "message": ... } shape.
		if (bindingResult.hasErrors()) {
			FieldError firstError = bindingResult.getFieldError();
			String message = firstError != null ? firstError.getDefaultMessage() : "Invalid request.";
			return badRequest(message);
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication != null ? authentication.getName() : null;
		Optional<User> optionalUser = username != null ? userRepository.findByUsername(username) : Optional.empty();
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new MessageResponse("Please log in again."));
		}

		User user = optionalUser.get();
		String newPassword = request.getNewPassword();

		if (!encoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new MessageResponse("Current password is incorrect."));
		}
		if (!newPassword.equals(request.getNewPasswordConfirm())) {
			return badRequest("New password and confirmation do not match.");
		}
		if (encoder.matches(newPassword, user.getPassword())) {
			return badRequest("New password must be different from the current password.");
		}
		if (newPassword.equalsIgnoreCase(LEGACY_DEFAULT_PASSWORD)) {
			return badRequest("This password is not allowed. Choose a different one.");
		}
		if (newPassword.toLowerCase().contains(user.getUsername().toLowerCase())) {
			return badRequest("Password must not contain your username.");
		}

		user.setPassword(encoder.encode(newPassword));
		user.setIsFirstTimeLogin(false);
		changePasswordServiceImpl.updateUser(user);
		logger.info("Password changed for user {}", user.getUsername());

		return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
	}

	private static ResponseEntity<MessageResponse> badRequest(String message) {
		return ResponseEntity.badRequest().body(new MessageResponse(message));
	}
}
