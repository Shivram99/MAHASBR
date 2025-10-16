package com.mahasbr.mapper;

import com.mahasbr.dto.DivisionDto;
import com.mahasbr.entity.DivisionMaster;

public class DivisionMapper {

    public static DivisionDto toDto(DivisionMaster entity) {
        return DivisionDto.builder()
                .divisionId(entity.getDivisionId())
                .divisionName(entity.getDivisionName())
                .divisionCode(entity.getDivisionCode())
                .isActive(entity.getIsActive())
                .build();
    }

    public static DivisionMaster toEntity(DivisionDto dto) {
        return DivisionMaster.builder()
                .divisionId(dto.getDivisionId())
                .divisionName(dto.getDivisionName())
                .divisionCode(dto.getDivisionCode())
                .isActive(dto.getIsActive())
                .build();
    }
}
