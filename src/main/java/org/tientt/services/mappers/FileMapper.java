package org.tientt.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class FileMapper {

    public FileDTO toDTO(FileEntity fileEntity){
        if (fileEntity == null) return null;
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(fileEntity.getId());
        fileDTO.setName(fileEntity.getName());
        fileDTO.setContent(fileEntity.getContent());
        fileDTO.setCreatedAt(fileEntity.getCreatedAt());
        fileDTO.setUpdatedAt(fileEntity.getUpdatedAt());
        fileDTO.setSize(fileEntity.getSize());
        fileDTO.setType(fileEntity.getType());
        if (fileEntity.getChildren() != null){
            fileDTO.setChildren(fileEntity.getChildren().stream().map(this::toDTONoChild).collect(Collectors.toList()));
        }
        return fileDTO;
    };

    private FileDTO toDTONoChild(FileEntity fileEntity){
        if (fileEntity == null) return null;
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(fileEntity.getId());
        fileDTO.setName(fileEntity.getName());
        fileDTO.setContent(fileEntity.getContent());
        fileDTO.setCreatedAt(fileEntity.getCreatedAt());
        fileDTO.setUpdatedAt(fileEntity.getUpdatedAt());
        fileDTO.setSize(fileEntity.getSize());
        fileDTO.setType(fileEntity.getType());
        return fileDTO;
    }
}
