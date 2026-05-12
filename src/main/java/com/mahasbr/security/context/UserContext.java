package com.mahasbr.security.context;

import java.util.Set;

public record UserContext(
        Long userId,
        Long registryId,
        Long districtId,
        String divisionCode,
        Set<String> roles,
        String username
) {}
