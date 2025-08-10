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

    private String fileType;

    private long fileSize;

    private LocalDateTime uploadedAt;

    @Column(length = 255)
    private String category;     // main category

    @Column(length = 255)
    private String subcategory;

    @OneToOne(mappedBy = "metadata", cascade = CascadeType.ALL,  orphanRemoval = true)
    private DocumentContent content;

    // a custom one:
    @Override
    public String toString() {
        return "DocumentMetadata{" +
                "id=" + id +
                ", originalFilename='" + originalFilename + '\'' +
                ", title='" + title + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", uploadedAt=" + uploadedAt +
                ", hasContent=" + (content != null && content.getId() != null) +
                '}';
    }

    public String hasCategory() {
        return "main category: " + category
                + ", subcategory: " + subcategory;
    }
}
