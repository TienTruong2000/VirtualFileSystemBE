package org.tientt.controllers.files;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tientt.constants.ApiEndPoint;
import org.tientt.controllers.payloads.CreateFileRequest;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.TextFileService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class TextFileController {
    private final TextFileService textFileService;


    @PostMapping(ApiEndPoint.TextFile.TEXT_FILE_END_POINT)
    public ResponseEntity<?> create(@RequestBody @Valid CreateFileRequest request) {
        FileDTO newFile = textFileService.create(request.getPath(), request.getContent());
        return ResponseEntity.ok(newFile);
    }
}
