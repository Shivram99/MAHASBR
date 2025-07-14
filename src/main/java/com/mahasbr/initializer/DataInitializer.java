package com.mahasbr.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.ERole;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Add ROLE_ADMIN if not present
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(ERole.ROLE_ADMIN);
                    return roleRepository.save(role);
                });

        // 2. Add admin user if not present
        String adminEmail = "admin@example.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail(adminEmail);
            adminUser.setPhoneNo("9999999999");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // secure password

            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
        }
    }
}
