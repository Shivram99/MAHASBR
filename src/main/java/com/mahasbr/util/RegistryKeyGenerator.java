package com.mahasbr.util;
import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.mahasbr.security.context.UserContext;
import com.mahasbr.service.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Component("registryKeyGenerator")
@RequiredArgsConstructor
public class RegistryKeyGenerator implements KeyGenerator {

    private final CurrentUserService currentUserService;

    @Override
    public Object generate(Object target, Method method, Object... params) {

        Long cursor = (Long) params[0];
        int size = (int) params[1];

        UserContext user = currentUserService.getCurrentUser();

        return String.format(
                "%s-%s-%s-%s",
                user.roles(),
                user.userId(),
                cursor,
                size
        );
    }
}
