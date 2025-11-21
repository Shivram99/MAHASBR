package com.mahasbr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.mapper.RegistryApiAuthConfigMapper;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;

@Service
public class RegistryApiAuthConfigServiceImpl implements RegistryApiAuthConfigService {

    @Autowired
    private RegistryApiAuthConfigRepository repository;

    @Autowired
    private RegistryApiAuthConfigMapper mapper;

    @Override
    public RegistryApiAuthConfigResponse create(RegistryApiAuthConfigRequest request) {
        RegistryApiAuthConfigEntity entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public RegistryApiAuthConfigResponse update(Long id, RegistryApiAuthConfigRequest request) {
        RegistryApiAuthConfigEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        entity.setServiceName(request.getServiceName());
        entity.setServiceNameCode(request.getServiceNameCode());
        entity.setAuthUrl(request.getAuthUrl());
        entity.setAuthUrlMethod(request.getAuthUrlMethod());
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setApiData(request.getApiData());
        entity.setApiDataMethod(request.getApiDataMethod());
        entity.setActive(request.getActive());

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public RegistryApiAuthConfigResponse getById(Long id) {
        RegistryApiAuthConfigEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        RegistryApiAuthConfigEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        repository.delete(entity);
    }

    @Override
    public List<RegistryApiAuthConfigResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RegistryApiAuthConfigEntity getByServiceNameCode(String serviceNameCode) {
        return repository.findByServiceNameCode(serviceNameCode)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}
