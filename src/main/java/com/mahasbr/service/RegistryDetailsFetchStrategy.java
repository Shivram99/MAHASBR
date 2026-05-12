package com.mahasbr.service;

import java.util.Set;

import org.springframework.data.domain.Slice;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.security.context.UserContext;

public interface RegistryDetailsFetchStrategy {

    boolean supports(Set<String> roles);

    Slice<MstRegistryDetailsPageEntity> fetch(
            Long cursor,
            int size,
            UserContext user
    );

}
