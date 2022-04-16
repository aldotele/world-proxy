package com.world.worldproxy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class CountryMapService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    public String getMapsByCountryName(String country) {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + country, String.class);
        return response.getBody();
    }
}
