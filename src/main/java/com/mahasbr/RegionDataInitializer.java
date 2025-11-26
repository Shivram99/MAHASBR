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
	 private  RoleRepository roleRepository;

	 @Override
	    public void run(String... args) {

	        for (ERole roleEnum : ERole.values()) {

	            Optional<Role> existingRole = roleRepository.findByName(roleEnum);

	            if (!existingRole.isPresent()) { // If role not found → insert
	                Role role = new Role();
	                role.setName(roleEnum);

	                roleRepository.save(role);
	                System.out.println("✔ Role created: " + roleEnum);
	            } else {
	                System.out.println("ℹ Role already exists: " + roleEnum);
	            }
	        }

	        System.out.println("🎯 Roles initialization finished!");
	    }
	
//    @Autowired
//    private RegionRepository regionRepository;

//    @Override
//    public void run(String... args) throws Exception {
////        if (regionRepository.count() == 0) {
////            regionRepository.save(new RegionEntity("Amravati", 1L));
////            regionRepository.save(new RegionEntity("Aurangabad", 1L));
////            regionRepository.save(new RegionEntity("Konkan", 1L));
////            regionRepository.save(new RegionEntity("Nagpur", 1L));
////            regionRepository.save(new RegionEntity("Nashik", 1L));
////            regionRepository.save(new RegionEntity("Pune", 1L));
////        }
    
}
