package com.mawkszuxz.docmanagment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // --- FIX APPLIED HERE ---
    // Replaced @Lob with @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    // This explicitly tells Hibernate to treat this field as a standard long string
    // and store its content directly in the 'content' column, overriding the
    // JDBC driver's potential default behavior of using large objects (OIDs).
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne
    @JoinColumn(name = "metadata_id", nullable = false)
    private DocumentMetadata metadata;
}

