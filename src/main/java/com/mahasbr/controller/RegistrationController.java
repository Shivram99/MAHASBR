package com.mahasbr.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DepartmentMst;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.ERole;
import com.mahasbr.model.SignupRequest;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.DepartmentMstService;
import com.mahasbr.util.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class RegistrationController {
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
  private  DepartmentMstService departmentMstService;



  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }


 if (userRepository.existsByEmail(signUpRequest.getEmail())) { return
	 ResponseEntity.badRequest().body(new
	  MessageResponse("Error: Email is already in use!")); }
	 
    // Create new user's account
    User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()),signUpRequest.getEmail(),signUpRequest.getPhoneNo());
    
    Optional<DepartmentMst>  departmentMst =departmentMstService.findDepartmentById(signUpRequest.getDepartmentId());
    
    if(departmentMst.isPresent()) {
    	 user.setDepartment(departmentMst.get());
    }
   
    String strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
        switch (strRoles) {
        case "ROLE_ADMIN":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "ROLE_MODERATOR":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        case "ROLE_DEVELOPER":
            Role devRole = roleRepository.findByName(ERole.ROLE_DEVELOPER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(devRole);

            break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}