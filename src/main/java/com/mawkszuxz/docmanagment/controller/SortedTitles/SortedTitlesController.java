package com.mawkszuxz.docmanagment.controller.SortedTitles;

import com.mawkszuxz.docmanagment.service.DocumentService;
import com.mawkszuxz.docmanagment.service.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/files")
public class SortedTitlesController {

//    private final DocumentService documentsService;
    private final SortingService sortingService;

    @Autowired
    public SortedTitlesController(DocumentService documentService, SortingService sortingService) {
//        this.documentsService = documentService;
        this.sortingService = sortingService;
    }
    /**
     * Retrieves a paginated list of document titles, sorted alphabetically.
     *
     * @param page The page number to retrieve (0-indexed, defaults to 0).
     * @param size The number of titles per page (defaults to 10).
     * @return A Page object containing the document titles.
     */
    @GetMapping("/titles")
    public ResponseEntity<Page<String>> getSortedDocumentTitles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("[DocumentController] Request received for sorted titles. Page: " + page + ", Size: " + size);
        try{
            Page<String> titlesPage = sortingService.getDocumentTitlesSorted(page, size);
            if(titlesPage.isEmpty() && page > 0 && titlesPage.getTotalPages() > 0 && page >= titlesPage.getTotalPages()) {
                System.out.println("[DocumentController] Requested page " + page + " is out of bounds. Total pages: " + titlesPage.getTotalPages());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty()); // Or handle as you see fit
            }
            System.out.println("[DocumentController] Returning " + titlesPage.getNumberOfElements() + " titles for page " + page);
            return ResponseEntity.ok(titlesPage);
        }catch (Exception e) {
            System.err.println("[DocumentController] Error fetching sorted titles: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving document titles: " + e.getMessage(), e);
        }
    }
}
