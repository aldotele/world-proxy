package com.world.worldproxy.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class CountrySummaryService {

    RestTemplate restTemplate = new RestTemplate();
    String allCountriesUrl = "https://restcountries.com/v3.1/all";

    public String getAllCountriesRawData() {
        ResponseEntity<String> response = restTemplate.getForEntity(allCountriesUrl, String.class);
        return response.getBody();
    }

}
