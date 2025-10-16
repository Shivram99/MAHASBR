package com.mahasbr.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.RegistryMasterResponse;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.RegistryMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistryMasterService {

    private final RegistryMasterRepository registryMasterRepository;

    public List<RegistryMasterResponse> getAllRegistries() {
        return registryMasterRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RegistryMasterResponse getRegistryById(Long id) {
        RegistryMasterEntity entity = registryMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registry not found with id: " + id));
        return mapToResponse(entity);
    }

    public RegistryMasterResponse createRegistry(RegistryMasterResponse request) {
        RegistryMasterEntity entity = new RegistryMasterEntity();
        entity.setRegistryNameEn(request.getRegistryNameEn());
        entity.setRegistryNameMr(request.getRegistryNameMr());
        entity.setStatus(request.getStatus());
        RegistryMasterEntity saved = registryMasterRepository.save(entity);
        return mapToResponse(saved);
    }

    public RegistryMasterResponse updateRegistry(Long id, RegistryMasterResponse request) {
        RegistryMasterEntity entity = registryMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registry not found with id: " + id));

        entity.setRegistryNameEn(request.getRegistryNameEn());
        entity.setRegistryNameMr(request.getRegistryNameMr());
        entity.setStatus(request.getStatus());
        RegistryMasterEntity updated = registryMasterRepository.save(entity);
        return mapToResponse(updated);
    }

    public void deleteRegistry(Long id) {
        RegistryMasterEntity entity = registryMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registry not found with id: " + id));
        registryMasterRepository.delete(entity);
    }

    private RegistryMasterResponse mapToResponse(RegistryMasterEntity entity) {
        return new RegistryMasterResponse(
                entity.getId(),
                entity.getRegistryNameEn(),
                entity.getRegistryNameMr(),
                entity.getStatus()
        );
    }
}
