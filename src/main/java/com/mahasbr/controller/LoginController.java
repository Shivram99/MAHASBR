package com.mahasbr.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.UserMapper;
import com.mahasbr.dto.UserDto;
import com.mahasbr.entity.User;
import com.mahasbr.model.LoginRequest;
import com.mahasbr.repository.AuditLogRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.response.JwtResponse;
import com.mahasbr.service.RefreshTokenService;
import com.mahasbr.service.UserDetailsImpl;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private final AuditLogRepository auditLogRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;


	
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession httpSession,
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

            // Fetch roles
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // Fetch user entity with relations
            User userEntity = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Map entity → DTO
            UserDto userDto = userMapper.toUserDto(userEntity);
            userDto.setRoles(new HashSet<>(roles));
            userDto.setIsFirstTimeLogin(userDetails.getIsFirstTimeLogin());

            // Return JWT + UserDto
            return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", userDto));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }




	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response, Authentication auth) {
		refreshTokenService.revoke(auth.getName());

		ResponseCookie cookie = ResponseCookie.from("refresh", "").httpOnly(true).secure(true).sameSite("Strict")
				.path("/").maxAge(0).build();

		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		return ResponseEntity.ok("Logged out successfully");
	}

}