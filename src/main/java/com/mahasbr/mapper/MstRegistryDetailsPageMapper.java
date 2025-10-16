package com.mahasbr.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Mapper(componentModel = "spring", imports = { DateTimeFormatter.class })
public interface MstRegistryDetailsPageMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    MstRegistryDetailsPagesDTO toResponse(MstRegistryDetailsPageEntity entity);
    
    List<MstRegistryDetailsPageEntity> toEntityList(List<MstRegistryDetailsPagesDTO> dtos);
}