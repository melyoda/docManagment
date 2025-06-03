package com.mawkszuxz.docmanagment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "document_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne
    @JoinColumn(name = "metadata_id", nullable = false)
    private DocumentMetadata metadata;
}

