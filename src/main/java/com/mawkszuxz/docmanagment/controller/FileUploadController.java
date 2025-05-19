package com.mawkszuxz.docmanagment.controller;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "file.upload-dir";


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try{
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Could not create upload directory");
                }
            }

            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            DocumentMetadata documentMetadata = new DocumentMetadata();
            documentMetadata.setTitle(file.getOriginalFilename());
            documentMetadata.setId("ID: " + file.getOriginalFilename());
            documentMetadata.setUploadedAt(LocalDateTime.now());
            documentMetadata.setSize(file.getSize());

            System.out.println("Doc " + documentMetadata.toString());


            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}
