package com.mawkszuxz.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class SearchService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${SEARCH.API.URL}")
    private String searchApiUrl;

    public List<Map<String,Object>> search(String keyword) {

        String url = UriComponentsBuilder
                .fromHttpUrl(searchApiUrl)
                .queryParam("query", keyword)   // UriComponentsBuilder does the encoding once
                .build()
                .toUriString();

        ParameterizedTypeReference<List<Map<String,Object>>> type =
                new ParameterizedTypeReference<>() {};

        return restTemplate.exchange(url, HttpMethod.GET, null, type)
                .getBody();
    }
}
