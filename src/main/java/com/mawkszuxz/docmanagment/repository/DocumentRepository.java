package com.mawkszuxz.docmanagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mawkszuxz.docmanagment.model.DocumentMetadata;

public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
//custom query methods here if needed

}
