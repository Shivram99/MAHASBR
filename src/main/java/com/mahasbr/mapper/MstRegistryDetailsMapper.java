package com.mahasbr.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Mapper(componentModel = "spring")
public interface MstRegistryDetailsMapper {

    @Mapping(target = "siNo", ignore = true) // ignore DB id
    @Mapping(target = "regUserId", ignore = true) // set manually later
    MstRegistryDetailsPageEntity dtoToEntity(MstRegistryDetailsPagesDTO dto);

    List<MstRegistryDetailsPageEntity> dtoListToEntityList(List<MstRegistryDetailsPagesDTO> dtoList);

    /**
     * Refreshes an existing row from a re-synced record. Identity fields are never overwritten:
     * the row keeps its id, BRN, owner registry and dedupe key.
     */
    @Mapping(target = "siNo", ignore = true)
    @Mapping(target = "regUserId", ignore = true)
    @Mapping(target = "brnNo", ignore = true)
    @Mapping(target = "sourceRecordKey", ignore = true)
    @Mapping(target = "locationCode", ignore = true)
    @Mapping(target = "createdUserId", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "createdIp", ignore = true)
    @Mapping(target = "createdUserAgent", ignore = true)
    void updateEntityFromDto(MstRegistryDetailsPagesDTO dto, @MappingTarget MstRegistryDetailsPageEntity entity);
}
