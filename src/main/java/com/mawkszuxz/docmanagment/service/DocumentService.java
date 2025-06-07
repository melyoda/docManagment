package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.model.DocumentContent;
import com.mawkszuxz.docmanagment.repository.DocumentTitleView;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import com.mawkszuxz.docmanagment.repository.DocumentRepository;
import com.mawkszuxz.docmanagment.util.TikaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;// For specifying sort order if needed more explicitly


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;




@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    // Not strictly needed if cascade is working, but good to have if you need to interact with it directly.
    // @Autowired
    // private DocumentContentRepository documentContentRepository;

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    public void processAndSave(MultipartFile file) throws IOException {
        //some file validation
        if (file.isEmpty()) {
            System.err.println("Upload failed: File is empty.");
            throw new IOException("Uploaded file is empty");
        }

        String unsafeOriginalFilename = file.getOriginalFilename();
        if (unsafeOriginalFilename == null || unsafeOriginalFilename.isBlank()) {
            System.err.println("Upload failed: File has no name.");
            throw new IOException("File has no name");
        }

        //--- Enhanced Filename Sanitization ---
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
        // --- End of Enhanced Filename Sanitization ---

        String originalFilename = sanitizedFilename;

        //Log file information
        System.out.println("--- Processing File ---");
        System.out.println("Sanitized Original Filename for processing: " + originalFilename);
        System.out.println("Content Type: " + file.getContentType());
        System.out.println("Size: " + file.getSize() + " bytes");

        //Create upload dir
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            System.out.println("Creating upload directory: " + uploadPath);
            Files.createDirectories(uploadPath);
        }

        //Save file to disk
        Path filePath = uploadPath.resolve(originalFilename);
        try {
            System.out.println("Saving uploaded file to: " + filePath);
            file.transferTo(filePath.toFile());
            System.out.println("File saved successfully.");
        }catch (IOException e) {
            System.err.println("Error saving uploaded file to disk: " + e.getMessage());
            throw new IOException("Could not save file " + originalFilename + ": " + e.getMessage(), e);
        }

        //Parse title using TikaUtils
        TikaUtils.DocumentData extractedData;
        try (InputStream stream = Files.newInputStream(filePath)) {
            System.out.println("Attempting to extract data from file using TikaUtils: " + filePath);
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
                .filePath(filePath.toString())
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

        metadata.setContent(content);// Establish the bidirectional relationship
        System.out.println("--- Attempting to save to DB ---");
        System.out.println("Metadata to save : " + metadata.toString());
        // Avoid logging full content if very large
        System.out.println("Content snippet to save (first 100 chars): " + (contentToSave.length() > 100 ? contentToSave.substring(0,100) : contentToSave).replace("\n", " "));

        try {
            documentRepository.save(metadata);
            System.out.println("Successfully saved document metadata and content for: " + originalFilename);
        }catch (Exception e) {
            System.err.println("Database save failed for '" + originalFilename + "': " + e.getMessage());
            // e.printStackTrace(); // For detailed DB error
            throw new IOException("Could not save document metadata to database for '" + originalFilename + "': " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a paginated and sorted list of document metadata
     * Documents are sorted by their titles in ascending order
     *
     * @param pageNumber the page number to retrieve (0-indexed)
     * @param pageSize the number of documents per page.
     * @return A page object containing the document metadata for the requested page.
     */
    @Transactional(readOnly = true)
    public Page<DocumentMetadata> getDocumentsSortedByTitle(int pageNumber, int pageSize){
        // Ensure pageNumber is 0-based for Spring Data Pageable
        if (pageNumber < 0) {
            System.out.println("[DocumentService] Correcting invalid page number to 0.");
            pageNumber = 0;
        }
        // Ensure pageSize is at least 1
        if (pageSize < 1) {
            System.out.println("[DocumentService] Correcting invalid page size to default (10).");
            pageSize = 10; // Default to a reasonable page size
        }

        //Create a Pageable object. We want to sort by the title property in ascending oder
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("title").ascending());

        System.out.println("[DocumentService] Fetching full documents page: " + pageNumber + ", size: " + pageSize + ", sorted by title ASC.");

        Page<DocumentMetadata> documentPage = documentRepository.findAllByOrderByTitleAsc(pageable);

        System.out.println("[DocumentService] Found " + documentPage.getTotalElements() + " total full documents matching criteria.");
        System.out.println("[DocumentService] Returning " + documentPage.getNumberOfElements() + " full documents for page " + pageNumber + ".");
        System.out.println("[DocumentService] Total pages: " + documentPage.getTotalPages());

        return documentPage;
    }

    //=-----------------------------

    /**
     * Retrieves a paginated and sorted list of document titles.
     * Documents are sorted by their title in ascending order.
     *
     * @param pageNumber The page number to retrieve (0-indexed).
     * @param pageSize The number of document titles per page.
     * @return A Page object containing the document titles for the requested page.
     */
    @Transactional(readOnly = true) // Good practice for read operations
    public Page<String> getDocumentTitlesSorted(int pageNumber, int pageSize) {
        System.out.println("[DocumentService] Fetching projected document titles page: " + pageNumber + ", size: " + pageSize + ", sorted by title ASC.");
        if (pageNumber < 0) {
            System.out.println("[DocumentService] Correcting invalid page number to 0 for titles.");
            pageNumber = 0;
        }
        if (pageSize < 1) {
            System.out.println("[DocumentService] Correcting invalid page size to default (10) for titles.");
            pageSize = 10;
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("title").ascending());
        Page<DocumentTitleView> titlesProjectionPage = documentRepository.findProjectedByOrderByTitleAsc(pageable);
        System.out.println("[DocumentService] Found " + titlesProjectionPage.getTotalElements() + " projected titles.");
        // Map the Page<DocumentTitleView> to Page<String>
        return titlesProjectionPage.map(DocumentTitleView::getTitle);
    }


}

