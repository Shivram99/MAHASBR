package com.mahasbr.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.UserMapper;
import com.mahasbr.dto.UserDto;
import com.mahasbr.entity.AuditLog;
import com.mahasbr.entity.User;
import com.mahasbr.model.LoginRequest;
import com.mahasbr.repository.AuditLogRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.response.JwtResponse;
import com.mahasbr.service.AuditLogService;
import com.mahasbr.service.RefreshTokenService;
import com.mahasbr.service.TokenBlacklistService;
import com.mahasbr.service.UserDetailsImpl;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
	
	private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    
    private final AuditLogRepository auditLogRepository;
    
    private final AuditLogService auditService;
    
    private final TokenBlacklistService tokenBlacklistService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              BindingResult bindingResult,
                                              HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                    .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities()
                    .stream().map(a -> a.getAuthority()).toList();

            User userEntity = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            UserDto userDto = userMapper.toUserDto(userEntity);
            userDto.setRoles(new HashSet<>(roles));
            userDto.setIsFirstTimeLogin(userDetails.getIsFirstTimeLogin());

            auditService.logEvent(userDetails.getUsername(), "LOGIN_SUCCESS", true, request, "User logged in", 200,request.getMethod());

            return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", userDto));

        } catch (BadCredentialsException ex) {
            auditService.logEvent(loginRequest.getUsername(), "LOGIN_FAILED", false, request, ex.getMessage(), 401,request.getMethod());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                      HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("message", "No active session found");
            auditService.logEvent("Anonymous", "LOGOUT_FAILED", false, request, "No token provided", 400,request.getMethod());
            return ResponseEntity.ok(result);
        }

        String token = authHeader.substring(7);

        if (!jwtUtils.validateJwtToken(token)) {
            result.put("message", "Invalid or expired token");
            auditService.logEvent("Unknown", "LOGOUT_FAILED_INVALID_TOKEN", false, request, "Token invalid/expired", 401,request.getMethod());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        Instant expiry = jwtUtils.getExpirationDateFromJwtToken(token).toInstant();
        tokenBlacklistService.blacklistToken(token, expiry);

        auditService.logEvent(username, "LOGOUT_SUCCESS", true, request, "User logged out", 200,request.getMethod());

        result.put("message", "User logged out successfully");
        return ResponseEntity.ok(result);
    }
}

