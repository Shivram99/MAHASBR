package com.mahasbr.service;

import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.mahasbr.config.RegistryFetchRoleProperties;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.security.context.UserContext;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class StateDataStrategy implements RegistryDetailsFetchStrategy {

    private final RegistryFetchRoleProperties roleProperties;
    private final MstRegistryDetailsPageRepository repository;

    @Override
    public boolean supports(Set<String> roles) {
        return roles.stream()
                .anyMatch(roleProperties.getState()::contains);
    }

    @Override
    public Slice<MstRegistryDetailsPageEntity> fetch(
            Long cursor,
            int size,
            UserContext user) {

        Pageable pageable = PageRequest.of(0, size);

        return repository.findNextAll(
                cursor,
                pageable
        );
    }
}
