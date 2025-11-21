package com.mahasbr.mapper;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.RequestFormDTO;
import com.mahasbr.entity.RequestFormEntity;

@Component
public class RequestFormMapper {

    public RequestFormDTO toDTO(RequestFormEntity entity) {
    	RequestFormDTO dto = new RequestFormDTO();
        dto.setRequestId(entity.getRequestId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setMobile(entity.getMobile());
        dto.setDistrict(entity.getDistrict());
        dto.setMessage(entity.getMessage());
        return dto;
    }

    public List<RequestFormDTO> toDTOList(List<RequestFormEntity> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
