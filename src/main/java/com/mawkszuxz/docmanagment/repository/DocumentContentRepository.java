package com.mawkszuxz.docmanagment.repository;

import com.mawkszuxz.docmanagment.model.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
    // add custom queries later if needed
}