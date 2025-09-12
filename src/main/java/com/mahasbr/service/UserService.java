package com.mahasbr.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.RoleDto;
import com.mahasbr.dto.UserDto;
import com.mahasbr.dto.UserProfileDto;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.entity.UserProfileEntity;
import com.mahasbr.model.ERole;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Get all users
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Get user by ID
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Create user with default role and password
    public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setIsFirstTimeLogin(true);
        user.setPassword(passwordEncoder.encode("Pass@123"));

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        } else {
            for (String roleName : dto.getRoles()) {
                Role role = roleRepository.findByName(ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);

        // Set UserProfile
        if (dto.getUserProfile() != null) {
            UserProfileEntity profile = new UserProfileEntity();
            profile.setFullName(dto.getUserProfile().getFullName());
            profile.setOfficeName(dto.getUserProfile().getOfficeName());
            profile.setOfficeAddress(dto.getUserProfile().getOfficeAddress());
            profile.setMobileNumber(dto.getUserProfile().getMobileNumber());
            profile.setUser(user);
            user.setUserProfile(profile);
        }

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    // Update user
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setIsFirstTimeLogin(dto.getIsFirstTimeLogin());

        // Update roles
        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(ERole.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            existing.setRoles(roles);
        }

        // Update profile
        if (dto.getUserProfile() != null) {
            UserProfileEntity profile = existing.getUserProfile();
            if (profile == null) {
                profile = new UserProfileEntity();
                profile.setUser(existing);
            }
            profile.setFullName(dto.getUserProfile().getFullName());
            profile.setOfficeName(dto.getUserProfile().getOfficeName());
            profile.setOfficeAddress(dto.getUserProfile().getOfficeAddress());
            profile.setMobileNumber(dto.getUserProfile().getMobileNumber());
            existing.setUserProfile(profile);
        }

        return toDto(userRepository.save(existing));
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Convert entity to DTO
    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setIsFirstTimeLogin(user.getIsFirstTimeLogin());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));

        if (user.getUserProfile() != null) {
            UserProfileDto profileDto = new UserProfileDto();
            profileDto.setId(user.getUserProfile().getId());
            profileDto.setFullName(user.getUserProfile().getFullName());
            profileDto.setOfficeName(user.getUserProfile().getOfficeName());
            profileDto.setOfficeAddress(user.getUserProfile().getOfficeAddress());
            profileDto.setMobileNumber(user.getUserProfile().getMobileNumber());
            dto.setUserProfile(profileDto);
        }
        return dto;
    }
    
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDto(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
    }
}