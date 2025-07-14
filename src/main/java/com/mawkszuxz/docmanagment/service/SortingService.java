package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.repository.DocumentRepository;
import com.mawkszuxz.docmanagment.repository.DocumentTitleView;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



@Service
public class SortingService {

    @Autowired
    private DocumentRepository documentRepository;


    /**
     * Retrieves a paginated and sorted list of document titles.
     * Documents are sorted by their title in ascending order.
     *
     * @param pageNumber The page number to retrieve (0-indexed).
     * @param pageSize The number of document titles per page.
     * @return A Page object containing the document titles for the requested page.
     * returns a String of titles
     */
    @Transactional(readOnly = true)
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








