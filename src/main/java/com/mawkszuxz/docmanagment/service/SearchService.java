package com.mawkszuxz.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private RestTemplate restTemplate;

    public List search(String keyword) {
        // 👇 encode keyword and pass as query param
        String url = "http://localhost:3000/search?query=" + UriUtils.encodeQueryParam(keyword, StandardCharsets.UTF_8);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                List.class
        );

        return response.getBody();
    }
}
