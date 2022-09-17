package org.tientt.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tientt.constants.ApiEndPoint;
import org.tientt.models.dtos.FileDTO;
import org.tientt.services.interfaces.DirectoryService;

import java.util.List;

@RestController
@AllArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT)
    public ResponseEntity<?> getAll(){
        List<FileDTO> result = directoryService.getAll();
        return ResponseEntity.ok(result);
    }

    @PostMapping(ApiEndPoint.Directory.DIRECTORY_END_POINT)
    public ResponseEntity<?> create(@RequestParam String path){
        FileDTO newDirectory = directoryService.create(path, false);
        return ResponseEntity.ok(newDirectory);
    }
}
