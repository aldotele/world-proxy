package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Country;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class CountryService {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    public List<Country> getAllCountriesRawData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
        List<Country> countries = Arrays.asList(mapper.readValue(response.getBody(), Country[].class));
        return countries;
    }

}
