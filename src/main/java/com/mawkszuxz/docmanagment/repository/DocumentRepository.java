package com.mawkszuxz.docmanagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mawkszuxz.docmanagment.model.DocumentMetadata;

import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
//custom query methods here if needed

    //method for sorted and paginated results by title
    Page<DocumentMetadata> findAllByOrderByTitleAsc(Pageable pageable);

    Page<DocumentTitleView> findProjectedByOrderByTitleAsc(Pageable pageable); // New projection method


}

