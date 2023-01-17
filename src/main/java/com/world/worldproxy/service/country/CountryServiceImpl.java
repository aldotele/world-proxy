package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.model.Country;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    @Override
    public List<Country> getAllCountries() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
        List<Country> countries = Arrays.asList(objectMapper.readValue(response.getBody(), Country[].class));
        return countries;
    }

    @Override
    public Country getCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + countryName, String.class);
            JSONArray jsonArray = new JSONArray(response.getBody());
            Country country = objectMapper.readValue(jsonArray.get(0).toString(), Country.class);
            return country;
        } catch (HttpClientErrorException.NotFound e) {
            throw new CountryNotFoundException(countryName);
        }
    }

}
