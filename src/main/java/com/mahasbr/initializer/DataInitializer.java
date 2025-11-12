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
		createUserIfNotExists("SUPER_ADMIN", "superadmin@gmail.com",  "Pass@123", "ROLE_ADMIN");
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
