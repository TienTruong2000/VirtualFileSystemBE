package org.tientt.services.interfaces;

import org.tientt.models.dtos.FileDTO;

public interface TextFileService {
    FileDTO create(String path, String content);
}
