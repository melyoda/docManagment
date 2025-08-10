package com.mawkszuxz.docmanagment.service;



import com.mawkszuxz.docmanagment.model.DocumentContent;
//import com.mawkszuxz.docmanagment.repository.DocumentContentRepository;
import com.mawkszuxz.docmanagment.util.CategoryResult;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;



@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SyncWithSearchService syncWithSearchService;

    @Autowired
    private CategorizationService categorizationService;


    public void processAndSave(MultipartFile file) throws IOException {
        //some file validation
        String unsafeOriginalFilename = fileValidation(file);// file validation function simple
        /*--------------------------------------------------------*/
        String originalFilename = fileSanitization(unsafeOriginalFilename);//Enhanced Filename Sanitization
        /*--------------------------------------------------------*/

        /**
         * TODO: make these either a log file or delete
         */
        //Log file information
        System.out.println("--- Processing File ---");
        System.out.println("Sanitized Original Filename for processing: " + originalFilename);
        System.out.println("Content Type: " + file.getContentType());
        System.out.println("Size: " + file.getSize() + " bytes");

        /*--------------------------------------------------------*/

        //Parse title using TikaUtils
        TikaUtils.DocumentData extractedData;
        try (InputStream stream = file.getInputStream()) {
            System.out.println("Attempting to extract data from file using TikaUtils: " + originalFilename);
            extractedData = TikaUtils.extractData(stream, originalFilename);
        }catch(TikaException |SAXException e) {
            System.err.println("Tika parsing error for '" + originalFilename + "': " + e.getMessage());
            //Clean up the saved file if parsing fails catastrophically
            //Files.deleteIfExists(filePath);
            throw new IOException("Error parsing document '" + originalFilename + "': " + e.getMessage(), e);
        }catch (Exception e) {
            System.err.println("Unexpected error during data extraction for '" + originalFilename + "': " + e.getMessage());
            //e.printStackTrace(); // For detailed debug
            throw new IOException("Error extracting document data for '" + originalFilename + "': " + e.getMessage(), e);
        }

        System.out.println("---- Data Extracted by TikaUtils for: " + originalFilename + " ----");
        System.out.println("Extracted Title: '" + extractedData.title() + "'");
        System.out.println("Extracted Content Length: " + (extractedData.content() != null ? extractedData.content().length() : "null"));

        if(extractedData.content() != null && !extractedData.content().isEmpty()) {
            System.out.println("Extracted content preview (First 200 chars): " +
                    extractedData.content().substring(0,Math.min(extractedData.content().length(),200)).replace("\n", " "));
        }else {
            System.out.println("WARNING: Content extracted by TikaUtils is null or empty!");
        }
        System.out.println("--------------------------------------");

        //final decision for the title to be saved
        String finalTitle = extractedData.title();
        if(finalTitle == null || finalTitle.trim().isEmpty() || finalTitle.equals(originalFilename) || finalTitle.equals("Untitled Document")) {
            // If Tika's title is missing, generic, or just the filename, prefer the original filename if it's more descriptive.
            System.out.println("Tika title was generic or same as filename. Using original filename as final title: '" + originalFilename + "'");
            finalTitle = originalFilename;
        } else {
            System.out.println("Using title from Tika extraction: '" + finalTitle + "'");
        }

        // Build DocumentMetadata object + build DocumentContent
        DocumentMetadata metadata = DocumentMetadata.builder()
                .originalFilename(originalFilename)
                .title(finalTitle)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        String contentToSave = extractedData.content();
        if(contentToSave == null) {
            System.err.println("CRITICAL: Content to save is null fo file: " + originalFilename + ". Storing empty string to avoid DB error.");
            contentToSave = "";
        }else if (contentToSave.length() < 100 && contentToSave.contains("@") && !contentToSave.contains(" ")) {
            System.err.println("CRITICAL WARNING: Persisting content that looks like an object reference: '" + contentToSave + "'. Review TikaUtils output.");
        }

        DocumentContent content = DocumentContent.builder()
                .content(contentToSave)
                .metadata(metadata)
                .build();

        CategoryResult result = categorizationService.classify(contentToSave);

        // 2. Set both category and subcategory on the metadata object
        metadata.setCategory(result.getMainCategory());
        metadata.setSubcategory(result.getSubCategory());

        metadata.setContent(content);// Establish the bidirectional relationship
        System.out.println("--- Attempting to save to DB ---");
        System.out.println("Metadata to save : " + metadata);
        System.out.println("Categories added : "+ metadata.hasCategory());
        // Avoid logging full content if very large
       // System.out.println("Content snippet to save (first 100 chars): " + (contentToSave.length() > 100 ? contentToSave.substring(0,100) : contentToSave).replace("\n", " "));

        try {
            // 1. Save the document to the database first
            DocumentMetadata savedEntity = documentRepository.save(metadata);

            // 2. After successful save, trigger the sync via the new service
            System.out.println("Document saved to DB with ID: " + savedEntity.getId() + ". Now attempting to sync with Meilisearch.");
//          syncWithSearchService.syncWithSearchService(savedEntity);
            Long id = savedEntity.getId();
            syncWithSearchService.syncWithSearchServiceId(id);


        }catch (Exception e) {
            System.err.println("Database save failed for '" + originalFilename + "': " + e.getMessage());
            // e.printStackTrace(); // For detailed DB error
            throw new IOException("Could not save document metadata to database for '" + originalFilename + "': " + e.getMessage(), e);
        }
    }

    public String fileValidation(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            System.err.println("Upload failed: File is empty.");
            throw new IOException("Uploaded file is empty");
        }

        String unsafeOriginalFilename = file.getOriginalFilename();

        if (unsafeOriginalFilename == null || unsafeOriginalFilename.isBlank()) {
            System.err.println("Upload failed: File has no name.");
            throw new IOException("File has no name");
        }

        return unsafeOriginalFilename;

    }

    public String fileSanitization(String unsafeOriginalFilename) throws IOException {
        //Extract the base filename component. This helps strip directory paths.
        Path inputPath = Paths.get(unsafeOriginalFilename);
        String sanitizedFilename = inputPath.getFileName().toString();

        //Validate the extracted filename.
        // It must not be empty, ".", or ".." to prevent path traversal issues.
        if (sanitizedFilename.isEmpty() || sanitizedFilename.equals(".") || sanitizedFilename.equals("..")) {
            System.err.println("Upload failed: Invalid filename extracted after sanitization: '" + sanitizedFilename + "' from original: '" + unsafeOriginalFilename + "'");
            throw new IOException("Invalid filename: " + sanitizedFilename);
        }

        //Check for any lingering path separators (though getFileName() should handle this, this is an extra precaution).
        if (sanitizedFilename.contains("/") || sanitizedFilename.contains("\\")) {
            System.err.println("Upload failed: Filename contains path separator characters after sanitization: '" + sanitizedFilename + "' from original: '" + unsafeOriginalFilename + "'");
            throw new IOException("Invalid characters in filename (path separators found): " + sanitizedFilename);
        }

        return sanitizedFilename;
    }
}