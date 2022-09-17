package org.tientt.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tientt.constants.ApiEndPoint;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.DirectoryService;

import java.util.List;

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

    @PostMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT)
    public ResponseEntity<?> create(@RequestParam String path){
        FileDTO newDirectory = directoryService.create(path, false);
        return ResponseEntity.ok(newDirectory);
    }
}
