package com.world.worldproxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class CountryService {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    public String getAllCountriesRawData() {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
        return response.getBody();
    }

}
