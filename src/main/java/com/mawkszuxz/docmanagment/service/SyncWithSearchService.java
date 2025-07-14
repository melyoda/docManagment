package com.mawkszuxz.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SyncWithSearchService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${meilisearch.sync.url}")
    private String meiliSyncUrl;

    /**
     * Sends the ID of a newly saved document to the Node.js search service.
     */
    public void syncWithSearchServiceId(Long docId){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = Map.of("id", docId);   // ← only ID

            HttpEntity<Map<String, Object>> req =
                    new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(meiliSyncUrl, req, String.class);
            System.out.println("✓ queued doc " + docId + " for indexing");
        } catch (Exception e) {
            System.err.println("[Sync] could not queue doc " + docId +
                    " → " + e.getMessage());
        }
    }
}
