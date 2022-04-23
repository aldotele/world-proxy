package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Country;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
    public Country getCountry(String name) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + name, String.class);
        JSONArray jsonArray = new JSONArray(response.getBody());
        Country country = objectMapper.readValue(jsonArray.get(0).toString(), Country.class);
        return country;
    }

    @Override
    public String getMapsByCountry(String country) {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + country, String.class);
        JSONArray jsonArr = new JSONArray(response.getBody());
        JSONObject jsonObj = (JSONObject) jsonArr.get(0);
        return (String) jsonObj.getJSONObject("maps").toMap().get("googleMaps");
    }

    @Override
    public String getCapitalByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getCapital();
    }

    @Override
    public String getFlagByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getFlag();
    }

    @Override
    public List<Country> getCountriesByPopulationRange(BigDecimal minPopulation, BigDecimal maxPopulation) throws JsonProcessingException {
        List<Country> allCountries = getAllCountries();
        return allCountries.stream()
                .filter(country -> country.getPopulation().compareTo(minPopulation) > 0 & country.getPopulation().compareTo(maxPopulation) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCountryNeighbours(String country) throws JsonProcessingException {
        String acronym = getCountry(country).getAcronym();
        List<Country> allCountries = getAllCountries();
        List<Country> neighbours = allCountries.stream()
                .filter(c -> Objects.nonNull(c.getBorders()))
                .filter(cc -> cc.getBorders().contains(acronym))
                .collect(Collectors.toList());
        List<String> neighboursNames = neighbours.stream()
                .map(Country::getName)
                .collect(Collectors.toList());
        return neighboursNames;
    }
}
