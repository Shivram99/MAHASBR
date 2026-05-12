package com.mahasbr.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.User;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.security.context.UserContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public UserContext getCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Unauthenticated access");
        }

        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException("User not found: " + username));

        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());

        return new UserContext(
                user.getId(),
                user.getRegistry().getId(),
                user.getDistrict() != null ? user.getDistrict().getDistrictId() : null,
                user.getDivisionCode(),
                roles,
                username
        );
    }
}

//	public User getUser() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//		if (auth == null || !auth.isAuthenticated()) {
//			throw new IllegalStateException("Unauthenticated access");
//		}
//
//		String username = auth.getName();
//
//		return userRepository.findByUsername(username)
//				.orElseThrow(() -> new IllegalStateException("User not found: " + username));
//	}
//
//	public Long getRegistryId() {
//		return getUser().getRegistry().getId();
//	}
//
//	public String getDivisionCode() {
//		return getUser().getDivisionCode();
//	}
//
//	public Long getDistrictId() {
//		return getUser().getDistrict().getDistrictId();
//	}
//
//	public Set<String> getRoles() {
//		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
//				.map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//	}
//
//	public boolean hasRole(String role) {
//		return getRoles().contains(role);
//	}
//}
