package com.mawkszuxz.docmanagment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "document_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;

    private String title;

    @Column(name = "file_path")
    private String filePath;

    private String fileType;

    private long fileSize;

    private LocalDateTime uploadedAt;

    @OneToOne(mappedBy = "metadata", cascade = CascadeType.ALL)
    private DocumentContent content;

    @Override
    public String toString(){
        return "ID" + id
                + "\nTitle" + title
                + "\nOriginal Filename" + originalFilename
                + "\nFile Path" + filePath
                + "\nFile Type" + fileType
                + "\nFile Size" + fileSize
                + "\nUploaded At" + uploadedAt;
    }
}
