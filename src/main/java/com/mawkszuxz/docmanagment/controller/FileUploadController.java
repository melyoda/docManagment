package com.mawkszuxz.docmanagment.controller;


import com.mawkszuxz.docmanagment.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;


@RestController
@RequestMapping("/api/files")
public class FileUploadController {


    private final DocumentService documentsService;

   // Constructor injection is preferred
    @Autowired
    public FileUploadController(DocumentService documentsService) {
      this.documentsService = documentsService;
    }
    /**
     * Handles file uploads.
     * The uploaded file is processed and its metadata and content are saved.
     *
     * @param file The multipart file being uploaded.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }
        try {
            System.out.println("[DocumentController] Received file for upload: " + file.getOriginalFilename());
            documentsService.processAndSave(file);
            System.out.println("[DocumentController] File processed and saved successfully: " + file.getOriginalFilename());
            return ResponseEntity.ok("File '" + file.getOriginalFilename() + "' uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }catch (Exception e) {
            System.err.println("[DocumentController] Unexpected error during file upload for " + file.getOriginalFilename() + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), e);
        }
    }


}