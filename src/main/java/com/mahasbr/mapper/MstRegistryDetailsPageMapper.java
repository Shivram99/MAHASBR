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

    @Mapping(target = "dateOfRegistration",
             expression = "java(entity.getDateOfRegistration() != null ? entity.getDateOfRegistration().format(DATE_FORMATTER) : null)")
    @Mapping(target = "dateOfDeregistrationExpiry",
             expression = "java(entity.getDateOfDeregistrationExpiry() != null ? entity.getDateOfDeregistrationExpiry().format(DATE_FORMATTER) : null)")
    MstRegistryDetailsPagesDTO toResponse(MstRegistryDetailsPageEntity entity);
    
    @Mapping(target = "dateOfRegistration",
            expression = "java(entity.getDateOfRegistration() != null ? entity.getDateOfRegistration().format(DATE_FORMATTER) : null)")
   @Mapping(target = "dateOfDeregistrationExpiry",
            expression = "java(entity.getDateOfDeregistrationExpiry() != null ? entity.getDateOfDeregistrationExpiry().format(DATE_FORMATTER) : null)")
    List<MstRegistryDetailsPageEntity> toEntityList(List<MstRegistryDetailsPagesDTO> dtos);
}