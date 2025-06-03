package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.model.DocumentContent;
import com.mawkszuxz.docmanagment.repository.DocumentContentRepository;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import com.mawkszuxz.docmanagment.repository.DocumentRepository;
import com.mawkszuxz.docmanagment.util.TikaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;



@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentContentRepository documentContentRepository;

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    public void processAndSave(MultipartFile file) throws IOException {
        //some file validation
        if (file.isEmpty()) {
            throw new IOException("Uploaded file is empty");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new IOException("File has no name");
        }

        // Log file information
        System.out.println("Uploading file: " + file.getOriginalFilename());
        System.out.println("Content type: " + file.getContentType());
        System.out.println("Size: " + file.getSize());

        // Create upload dir
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(filePath.toFile());

        // Parse title using TikaUtils
        TikaUtils.DocumentData data;
        try (InputStream stream = Files.newInputStream(filePath)/*file.getInputStream()*/) {
            data = TikaUtils.extractData(stream);

            // 🔍 Debug print after parsing:
            System.out.println("Preview content: " + data.content().substring(0, 500));
            System.out.println("Content length: " + data.content().length());

        }catch(TikaException |SAXException e) {
            throw new IOException("Error parsing document" + e.getMessage());
        }catch (Exception e) {
            throw new IOException("Error extracting document data: " + e.getMessage());
        }

        String title = data.title();
        if (title == null || title.isEmpty()) {
            title = file.getOriginalFilename();
        }

        // Build DocumentMetadata object + build DocumentContent
        DocumentMetadata metadata = DocumentMetadata.builder()
                .originalFilename(file.getOriginalFilename())
                .title(title)
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        DocumentContent content = DocumentContent.builder()
                .content(data.content())
                .metadata(metadata)
                .build();

        metadata.setContent(content);

        // Save metadata to DB
        documentRepository.save(metadata);

//        documentContentRepository.save(content);
    }
}

