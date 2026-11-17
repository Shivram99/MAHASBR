package com.mahasbr.mapper;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.entity.RegistryMasterEntity;

@Component
public class RegistryApiAuthConfigMapper {

    /** Builds the response; {@code running} comes from the sync service, which tracks live runs. */
    public RegistryApiAuthConfigResponse toResponse(RegistryApiAuthConfigEntity entity, boolean running) {
        RegistryApiAuthConfigResponse response = new RegistryApiAuthConfigResponse();
        response.setId(entity.getId());
        response.setServiceName(entity.getServiceName());
        response.setServiceNameCode(entity.getServiceNameCode());
        response.setAuthUrl(entity.getAuthUrl());
        response.setAuthUrlMethod(entity.getAuthUrlMethod());
        response.setUsername(entity.getUsername());
        response.setPasswordSet(entity.getPassword() != null && !entity.getPassword().isEmpty());
        response.setApiData(entity.getApiData());
        response.setApiDataMethod(entity.getApiDataMethod());
        response.setActive(entity.getActive());

        RegistryMasterEntity registry = entity.getRegistry();
        if (registry != null) {
            response.setRegistryId(registry.getId());
            response.setRegistryName(registry.getRegistryNameEn() != null
                    ? registry.getRegistryNameEn()
                    : registry.getRegistryName());
        }
        response.setSyncFromDate(entity.getSyncFromDate());
        response.setLastSyncedTo(entity.getLastSyncedTo());
        response.setLastRunAt(entity.getLastRunAt());
        response.setLastRunStatus(entity.getLastRunStatus());
        response.setLastRunMessage(entity.getLastRunMessage());
        response.setRunning(running);
        return response;
    }
}
