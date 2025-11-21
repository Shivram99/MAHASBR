package com.mahasbr.service;

import java.util.List;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;

public interface RegistryApiAuthConfigService {

    RegistryApiAuthConfigResponse create(RegistryApiAuthConfigRequest request);

    RegistryApiAuthConfigResponse update(Long id, RegistryApiAuthConfigRequest request);

    RegistryApiAuthConfigResponse getById(Long id);

    void delete(Long id);

    List<RegistryApiAuthConfigResponse> getAll();

    RegistryApiAuthConfigEntity getByServiceNameCode(String serviceNameCode);
}
