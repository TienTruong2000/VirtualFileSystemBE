package org.tientt.controllers.files;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tientt.constants.ApiEndPoint;
import org.tientt.controllers.payloads.CreateFileRequest;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.TextFileService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class TextFileController {
    private final TextFileService textFileService;
    @PostMapping(ApiEndPoint.TextFile.TEXT_FILE_END_POINT)
    public ResponseEntity<?> create(@RequestBody @Valid CreateFileRequest request) {
        FileDTO newFile = textFileService.create(request.getPath(), request.getContent());
        return ResponseEntity.ok(newFile);
    }

    @GetMapping(ApiEndPoint.TextFile.TEXT_FILE_END_POINT)
    public ResponseEntity<?> getFileByPath(@NotNull @RequestParam String path){
        FileDTO file = textFileService.getFileByPath(path);
        return ResponseEntity.ok(file);
    }
}
