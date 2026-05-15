package com.mahasbr.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.LoggedInUserResponse;
import com.mahasbr.entity.User;
import com.mahasbr.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoggedInUserService {

    private static final List<String> ROLE_PRIORITY = List.of(
            "ROLE_ADMIN",
            "ROLE_DES_STATE",
            "ROLE_DES_REGION",
            "ROLE_DES_DISTRICT",
            "ROLE_REG_AUTH_API",
            "ROLE_REG_AUTH_CSV",
            "ROLE_MODERATOR",
            "ROLE_DEVELOPER",
            "ROLE_USER");

    private static final Map<String, Integer> ROLE_PRIORITY_INDEX = buildPriorityIndex();

    private final UserRepository userRepository;

    public LoggedInUserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equalsIgnoreCase(String.valueOf(authentication.getPrincipal()))) {
            throw new InsufficientAuthenticationException("Unauthenticated access");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<String> roles = authentication.getAuthorities().stream()
                .map((authority) -> authority.getAuthority())
                .distinct()
                .sorted(Comparator.comparingInt(this::resolveRolePriority))
                .collect(Collectors.toCollection(ArrayList::new));

        return LoggedInUserResponse.builder()
                .username(user.getUsername())
                .fullName(user.getUserProfile() != null ? user.getUserProfile().getFullName() : null)
                .email(user.getEmail())
                .roles(roles)
                .activeRole(resolveActiveRole(roles))
                .build();
    }

    private String resolveActiveRole(List<String> roles) {
        return roles.isEmpty() ? null : roles.get(0);
    }

    private int resolveRolePriority(String role) {
        return ROLE_PRIORITY_INDEX.getOrDefault(role, Integer.MAX_VALUE);
    }

    private static Map<String, Integer> buildPriorityIndex() {
        return ROLE_PRIORITY.stream()
                .collect(Collectors.toMap(role -> role, ROLE_PRIORITY::indexOf));
    }
}
