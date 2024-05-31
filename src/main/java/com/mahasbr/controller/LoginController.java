package com.mahasbr.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.AuditLog;
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
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,HttpSession httpSession,HttpServletRequest request) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    
    httpSession.setAttribute("MY_SESSION_MESSAGES", userDetails);
    
    
    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());
    

	String username =userDetails.getUsername();
	String ipAddress = request.getRemoteAddr();
	auditLogRepository.save(new AuditLog(username, "LOGIN_SUCCESS", LocalDateTime.now(), ipAddress));

    return ResponseEntity
        .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
  }

  
}