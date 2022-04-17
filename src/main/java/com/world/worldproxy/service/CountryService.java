package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Country;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class CountryService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    public List<Country> getAllCountriesRawData() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
        List<Country> countries = Arrays.asList(objectMapper.readValue(response.getBody(), Country[].class));
        return countries;
    }

    public Country getCountryByName(String name) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + name, String.class);
        JSONArray jsonArray = new JSONArray(response.getBody());
        Country country = objectMapper.readValue(jsonArray.get(0).toString(), Country.class);
        return country;
    }

}
