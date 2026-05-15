package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mahasbr.dto.LoggedInUserResponse;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.entity.UserProfileEntity;
import com.mahasbr.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoggedInUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private LoggedInUserService service;

    @BeforeEach
    void setUp() {
        service = new LoggedInUserService(userRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedUserDetails() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "n/a",
                Set.of(
                        new SimpleGrantedAuthority("ROLE_REG_AUTH_API"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserProfileEntity profile = new UserProfileEntity();
        profile.setFullName("Admin User");

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        Role regRole = new Role();
        regRole.setName("ROLE_REG_AUTH_API");

        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        user.setUserProfile(profile);
        user.setRoles(Set.of(adminRole, regRole));

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        LoggedInUserResponse response = service.getCurrentUser();

        assertEquals("admin", response.getUsername());
        assertEquals("Admin User", response.getFullName());
        assertEquals("admin@test.com", response.getEmail());
        assertEquals("ROLE_ADMIN", response.getActiveRole());
        assertEquals("ROLE_ADMIN", response.getRoles().get(0));
        assertEquals(2, response.getRoles().size());
    }

    @Test
    void shouldRejectUnauthenticatedAccess() {
        SecurityContextHolder.clearContext();

        assertThrows(InsufficientAuthenticationException.class, () -> service.getCurrentUser());
    }
}
