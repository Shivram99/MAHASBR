package com.mahasbr.initializer;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.ERole;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Create default admin user
	@Override
	public void run(String... args) throws Exception {
		createUserIfNotExists("admin", "admin@example.com",  "admin123", "ROLE_ADMIN");
		createUserIfNotExists("moderator", "moderator@example.com",  "mod123", "ROLE_MODERATOR");
		createUserIfNotExists("developer", "developer@example.com",  "dev123", "ROLE_DEVELOPER");
		createUserIfNotExists("user", "user@example.com",  "user123", "ROLE_USER");
		createUserIfNotExists("des_state", "state@example.com",  "state123", "ROLE_DES_STATE");
		createUserIfNotExists("des_region", "region@example.com", "region123", "ROLE_DES_REGION");
		createUserIfNotExists("des_district", "district@example.com", "district123", "ROLE_DES_DISTRICT");
		createUserIfNotExists("auth_api", "api@example.com", "api123", "ROLE_REG_AUTH_API");
		createUserIfNotExists("auth_csv", "csv@example.com", "csv123", "ROLE_REG_AUTH_CSV");
	}

	private void createUserIfNotExists(String username, String email,  String rawPassword, String roleName) {
	    if (!userRepository.existsByEmail(email)) {
	        try {
	            ERole roleEnum = ERole.valueOf(roleName); // Convert String to ERole
	            Optional<Role> roleOpt = roleRepository.findByName(roleEnum);
	            
	            if (roleOpt.isPresent()) {
	                User user = new User();
	                user.setUsername(username);
	                user.setEmail(email);
	                user.setPassword(passwordEncoder.encode(rawPassword));
	                user.setRoles(Collections.singleton(roleOpt.get()));

	                userRepository.save(user);
	                System.out.println("User created: " + username);
	            } else {
	                System.err.println("Role not found in DB: " + roleName);
	            }
	        } catch (IllegalArgumentException ex) {
	            System.err.println("Invalid role name: " + roleName);
	        }
	    }
	}

	
}
