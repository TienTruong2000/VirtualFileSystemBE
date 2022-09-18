package org.tientt.controllers.payloads;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MoveFileRequest {
    @NotNull
    private String sourcePath;
    @NotNull
    private String destinationPath;
}
