package com.mawkszuxz.docmanagment.controller;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import com.mawkszuxz.docmanagment.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final DocumentService documentsService;

    // Constructor injection is preferred
    public FileUploadController(DocumentService documentsService) {
        this.documentsService = documentsService;
    }

//    public static final String UPLOAD_DIR = "fileDir/";
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        try{
            //create upload dir if it doesn't exist
//            Path uploadPath = Paths.get(UPLOAD_DIR);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            //save file to disk
            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(filePath.toFile());

            //file MetaData
            DocumentMetadata metadata = DocumentMetadata.builder()
                    .originalFilename(file.getOriginalFilename())
                    .title("Placeholder for now: " + file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            //saving data
            documentsService.saveDoc(metadata);

            return ResponseEntity.ok("File '" + file.getOriginalFilename() + "' uploaded successfully.");
        }catch (IOException e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }


}