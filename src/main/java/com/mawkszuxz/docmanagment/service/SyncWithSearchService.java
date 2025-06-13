package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SyncWithSearchService {

    @Autowired
    private RestTemplate restTemplate;

//    @Value("${meilisearch.sync.url:http://localhost:3000/sync}")
      String meiliSyncUrl = "http://localhost:3000/sync";

    /**
     * Sends the data of a newly saved document to the Node.js search service.
     */
    public void syncWithSearchService(DocumentMetadata document) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create a payload that matches what your Node.js service expects.
            Map<String, Object> documentPayload = Map.of(
                    "id", document.getId(),
                    "title", document.getTitle(),
                    "content", document.getContent().getContent()
            );

            // Wrap the single document payload in a list for Meilisearch's batch endpoint.
            HttpEntity<List<Map<String, Object>>> request =
                    new HttpEntity<>(Collections.singletonList(documentPayload), headers);

            // Make the POST request to your Node.js service.
            ResponseEntity<String> response = restTemplate.postForEntity(meiliSyncUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Successfully synced document ID " + document.getId() + " with Meilisearch service.");
            } else {
                System.err.println("Failed to sync document ID " + document.getId() +
                        ". Search service responded with status: " + response.getStatusCode() +
                        " and body: " + response.getBody());
            }
        } catch (Exception e) {
            // Log the exception but do not re-throw it to prevent the entire upload from failing
            // if the search service is unavailable.
            System.err.println("[SyncWithSearchService] [CRITICAL]: Could not sync document ID " + document.getId() +
                    " with search service. Error: " + e.getMessage());

           // System.out.println("[SyncWithSearchService] " + document.getId() +"\n " + document.getTitle() +"\n " +document.getContent().getContent());
        }
    }
}
