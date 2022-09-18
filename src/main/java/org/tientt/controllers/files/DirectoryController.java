package org.tientt.controllers.files;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tientt.constants.ApiEndPoint;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.DirectoryService;

import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT + "/{id}")
    public ResponseEntity<?> getDirectoryById(@PathVariable long id){
        FileDTO result = directoryService.getById(id);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT)
    public ResponseEntity<?> getDirectoryByPath(@RequestParam @NotNull String path){
        FileDTO result = directoryService.getByPath(path);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @PostMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT)
    public ResponseEntity<?> create(@RequestParam @NotNull String path){
        FileDTO newDirectory = directoryService.create(path, false);
        return ResponseEntity.ok(newDirectory);
    }
}
