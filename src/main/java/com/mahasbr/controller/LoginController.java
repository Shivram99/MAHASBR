package com.mahasbr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.model.LoginRequest;
import com.mahasbr.repository.AuditLogRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.response.JwtResponse;
import com.mahasbr.service.UserDetailsImpl;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class LoginController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private AuditLogRepository auditLogRepository;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
			BindingResult bindingResult, HttpSession httpSession, HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errors); // send field-specific errors
		}

		try {
			// Authenticate credentials
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			// Save authentication context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate JWT
			String jwt = jwtUtils.generateJwtToken(authentication);

			// Get user details
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			httpSession.setAttribute("MY_SESSION_MESSAGES", userDetails);

			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

			// Return success response
			return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles,
					userDetails.getIsFirstTimeLogin()));

		} catch (BadCredentialsException ex) {
			// Return a generic error to avoid giving attackers hints
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Invalid username or password"));
		}
	}

}