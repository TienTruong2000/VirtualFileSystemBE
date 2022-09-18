package org.tientt.services.interfaces;

import org.tientt.models.dtos.FileDTO;

public interface FileService {
    FileDTO deleteByPath(String path);

    FileDTO moveFile(String sourcePath, String destinationPath);
}
