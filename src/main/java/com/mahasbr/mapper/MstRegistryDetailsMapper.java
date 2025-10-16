package com.mahasbr.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Mapper(componentModel = "spring")
public interface MstRegistryDetailsMapper {

    @Mapping(target = "siNo", ignore = true) // ignore DB id
    @Mapping(target = "regUserId", ignore = true) // set manually later
    MstRegistryDetailsPageEntity dtoToEntity(MstRegistryDetailsPagesDTO dto);

    List<MstRegistryDetailsPageEntity> dtoListToEntityList(List<MstRegistryDetailsPagesDTO> dtoList);
}
