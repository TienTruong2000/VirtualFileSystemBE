package org.tientt.controllers.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tientt.constants.ApiEndPoint;
import org.tientt.controllers.payloads.MoveFileRequest;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.FileService;

import javax.validation.Valid;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping(ApiEndPoint.File.MOVE_FILE)
    public ResponseEntity<?> moveFile(@RequestBody @Valid MoveFileRequest request) {
        FileDTO result = fileService.moveFile(request.getSourcePath(), request.getDestinationPath());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(ApiEndPoint.File.FILE_END_POINT)
    public ResponseEntity<?> deleteFile(@RequestParam String path) {
        FileDTO result = fileService.deleteByPath(path);
        return ResponseEntity.ok(result);
    }
}
