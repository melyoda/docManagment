package com.mawkszuxz.docmanagment.controller;


import com.mawkszuxz.docmanagment.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/files")
public class FileUploadController {


    private final DocumentService documentsService;

   // Constructor injection is preferred
    public FileUploadController(DocumentService documentsService) {
      this.documentsService = documentsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        try {
            documentsService.processAndSave(file);
            return ResponseEntity.ok("File '" + file.getOriginalFilename() + "' uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }


}