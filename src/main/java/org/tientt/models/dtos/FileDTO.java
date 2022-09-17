package org.tientt.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tientt.models.entities.FileType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private long id;
    private String name;
    private long createdAt;
    private long updatedAt;
    private FileType type;
    private String content;
    private List<FileDTO> children;
}
