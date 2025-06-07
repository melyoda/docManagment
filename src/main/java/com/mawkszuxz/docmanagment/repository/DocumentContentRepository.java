package com.mawkszuxz.docmanagment.repository;

import com.mawkszuxz.docmanagment.model.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
    // add custom queries later if needed
}