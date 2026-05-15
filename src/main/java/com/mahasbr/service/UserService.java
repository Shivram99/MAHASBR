package com.mahasbr.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mahasbr.dto.RoleDto;
import com.mahasbr.dto.UserDto;
import com.mahasbr.dto.UserProfileDto;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.entity.UserProfileEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.DivisionRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DivisionRepository divisionRepository;
    private final UserRoleLocationPolicyService userRoleLocationPolicyService;

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserDto createUser(@Valid UserDto dto) {
        validateUniqueUsername(dto.getUsername(), null);
        validateUniqueEmail(dto.getEmail(), null);

        UserRoleLocationPolicyService.ValidatedUserLocation location =
                userRoleLocationPolicyService.validateAndResolve(dto);

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        user.setIsFirstTimeLogin(true);
        user.setPassword(passwordEncoder.encode("Pass@123"));
        user.setRoles(resolveRoles(dto.getRoles()));
        user.setRegistry(location.registry());
        user.setDivisionCode(location.divisionCode());
        user.setDistrict(location.district());
        user.setUserProfile(buildUserProfile(dto.getUserProfile(), user));

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        validateUniqueUsername(dto.getUsername(), id);
        validateUniqueEmail(dto.getEmail(), id);

        UserRoleLocationPolicyService.ValidatedUserLocation location =
                userRoleLocationPolicyService.validateAndResolve(dto);

        existing.setUsername(dto.getUsername().trim());
        existing.setEmail(dto.getEmail().trim());
        existing.setIsFirstTimeLogin(dto.getIsFirstTimeLogin());
        existing.setRoles(resolveRoles(dto.getRoles()));
        existing.setRegistry(location.registry());
        existing.setDivisionCode(location.divisionCode());
        existing.setDistrict(location.district());
        existing.setUserProfile(buildUserProfile(dto.getUserProfile(), existing));

        return toDto(userRepository.save(existing));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setIsFirstTimeLogin(user.getIsFirstTimeLogin());
        dto.setRegistryId(user.getRegistry() != null ? user.getRegistry().getId() : null);
        dto.setDistrictId(user.getDistrict() != null ? user.getDistrict().getDistrictId() : null);
        dto.setDivisionCode(user.getDivisionCode() != null ? user.getDivisionCode() : null);
        if (StringUtils.hasText(user.getDivisionCode())) {
            divisionRepository.findByDivisionCode(user.getDivisionCode())
                    .ifPresent(division -> dto.setDivisionId(division.getDivisionId()));
        }


        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(r -> r.getName())
                    .collect(Collectors.toSet()));
        }

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
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();

        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("At least one role must be selected."));
            roles.add(defaultRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        return roles;
    }

    private UserProfileEntity buildUserProfile(UserProfileDto dto, User user) {
        if (dto == null) {
            return null;
        }

        UserProfileEntity profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfileEntity();
            profile.setUser(user);
        }

        profile.setFullName(dto.getFullName().trim());
        profile.setOfficeName(dto.getOfficeName().trim());
        profile.setOfficeAddress(dto.getOfficeAddress() != null ? dto.getOfficeAddress().trim() : null);
        profile.setMobileNumber(dto.getMobileNumber().trim());

        return profile;
    }

    private void validateUniqueUsername(String username, Long userId) {
        String normalizedUsername = username != null ? username.trim() : null;
        if (!StringUtils.hasText(normalizedUsername)) {
            return;
        }

        userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists.");
                });
    }

    private void validateUniqueEmail(String email, Long userId) {
        String normalizedEmail = email != null ? email.trim() : null;
        if (!StringUtils.hasText(normalizedEmail)) {
            return;
        }

        userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists.");
                });
    }
}
