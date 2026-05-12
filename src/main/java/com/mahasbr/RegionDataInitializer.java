package com.mahasbr;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.Role;
import com.mahasbr.model.ERole;
import com.mahasbr.repository.RoleRepository;



@Component
public class RegionDataInitializer implements CommandLineRunner {
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
//		createRoleIfNotExists(ERole.ROLE_REGION_ADMIN.name());
//		createRoleIfNotExists(ERole.ROLE_REGION_USER.name());
	}

	private void createRoleIfNotExists(String roleName) {
		Optional<Role> roleOpt = roleRepository.findByName(roleName);
		if (!roleOpt.isPresent()) {
			Role role = new Role();
			role.setName(roleName);
			roleRepository.save(role);
			System.out.println("Role created: " + roleName);
		}
	}
}
