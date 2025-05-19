package com.mawkszuxz.docmanagment.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {
    private String id;
    private String title;
    private String originalFilename;
    //private String content;
    private long size;
    private LocalDateTime uploadedAt;

    public DocumentMetadata(LocalDateTime uploadedAt, long size, String originalFilename, String title) {
        this.uploadedAt = uploadedAt;
        this.size = size;
        this.originalFilename = originalFilename;
        this.title = title;
    }

    @Override
    public String toString(){
        return "ID: " + id + " Title: " + title + " Size: " + size + " Uploaded at: " + uploadedAt;
    }

}
/*
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Extracted from the document
    private String originalFilename;
    private String filePath; // Full path or cloud URL
    private Long size; // in bytes
    private LocalDateTime uploadedAt;

    // optional: content snippet or preview text
    // @Lob
    // private String contentPreview;

    // constructors, getters, setters...
}
 */