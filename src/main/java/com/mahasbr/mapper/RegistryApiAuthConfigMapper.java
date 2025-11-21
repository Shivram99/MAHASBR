package com.mahasbr.mapper;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;

@Component
public class RegistryApiAuthConfigMapper {

    public RegistryApiAuthConfigEntity toEntity(RegistryApiAuthConfigRequest request) {
        RegistryApiAuthConfigEntity entity = new RegistryApiAuthConfigEntity();
        entity.setServiceName(request.getServiceName());
        entity.setServiceNameCode(request.getServiceNameCode());
        entity.setAuthUrl(request.getAuthUrl());
        entity.setAuthUrlMethod(request.getAuthUrlMethod());
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setApiData(request.getApiData());
        entity.setApiDataMethod(request.getApiDataMethod());
        entity.setActive(request.getActive());
        return entity;
    }

    public RegistryApiAuthConfigResponse toResponse(RegistryApiAuthConfigEntity entity) {
        RegistryApiAuthConfigResponse response = new RegistryApiAuthConfigResponse();
        response.setId(entity.getId());
        response.setServiceName(entity.getServiceName());
        response.setServiceNameCode(entity.getServiceNameCode());
        response.setAuthUrl(entity.getAuthUrl());
        response.setAuthUrlMethod(entity.getAuthUrlMethod());
        response.setUsername(entity.getUsername());
        response.setPassword(entity.getPassword());
        response.setApiData(entity.getApiData());
        response.setApiDataMethod(entity.getApiDataMethod());
        response.setActive(entity.getActive());
        return response;
    }
}
