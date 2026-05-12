package com.mahasbr.service;

import java.util.List;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import com.mahasbr.config.RegistryFetchRoleProperties;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.security.context.UserContext;

import lombok.RequiredArgsConstructor;
@Component
@Order(4)
@RequiredArgsConstructor
public class RegAuthRegistryStrategy
        implements RegistryDetailsFetchStrategy {

    private final RegistryFetchRoleProperties roleProperties;
    private final MstRegistryDetailsPageRepository repository;

    @Override
    public boolean supports(Set<String> roles) {
        return roles.stream()
                .anyMatch(roleProperties.getRegAuth()::contains);
    }

    @Override
    public Slice<MstRegistryDetailsPageEntity> fetch(
            Long cursor,
            int size,
            UserContext user) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<MstRegistryDetailsPageEntity> rows =
                repository.findNextByRegUserId(
                        user.registryId(),
                        cursor,
                        pageable
                );

        boolean hasNext = rows.size() > size;

        List<MstRegistryDetailsPageEntity> content =
                hasNext ? rows.subList(0, size) : rows;

        return new SliceImpl<>(
                content,
                PageRequest.of(0, size),
                hasNext
        );
    }
}


