package org.tientt.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class FileMapper {

    public abstract FileDTO toDTO(FileEntity fileEntity);

    public abstract FileEntity toEntity(FileDTO fileDTO);
}
