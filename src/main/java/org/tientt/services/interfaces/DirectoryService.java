package org.tientt.services.interfaces;

import org.tientt.models.dtos.FileDTO;

import java.util.List;

public interface DirectoryService {
    FileDTO create(String path, boolean isCreateParent);

    FileDTO getById(String id);

    List<FileDTO> getAll();

    FileDTO move(String sourcePath, String destinationPath);

    FileDTO deleteFromPath(String path);

}
