package org.tientt.controllers.payloads;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateFileRequest {
    @NotNull
    private String path;
    @NotNull
    private String content;
}
