package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;
import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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
    public List<String> getCurrencyByCountry(String country) throws  JsonProcessingException {
        return getCountry(country).getCurrencies();
    }

    @Override
    public String getFlagByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getFlag();
    }

    @Override
    public List<Country> getCountriesByContinent(String continent) throws JsonProcessingException {
        return getAllCountries().stream()
                .filter(country -> country.getContinents().contains(WordUtils.capitalizeFully(continent)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Country> getCountriesByPopulationRange(BigDecimal minimum, BigDecimal maximum) throws JsonProcessingException, QueryParameterException {
        List<Country> allCountries = getAllCountries();
        // both min and max provided
        if (minimum != null & maximum != null) {
            if (minimum.compareTo(maximum) > 0) {
                throw new QueryParameterException("population range is not valid.");
            }
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(minimum) > 0 & country.getPopulation().compareTo(maximum) < 0)
                    .collect(Collectors.toList());
        }
        // only min provided
        else if (minimum != null & maximum == null) {
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(minimum) > 0)
                    .collect(Collectors.toList());
        }
        // only max provided
        else if (maximum != null & minimum == null) {
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(maximum) < 0)
                    .collect(Collectors.toList());
        }
        else {
            throw new QueryParameterException("at least one parameter between min and max population is required.");
        }
    }

    @Override
    public List<Country> getCountryNeighbours(String country) throws JsonProcessingException {
        String acronym = getCountry(country).getAcronym();
        List<Country> allCountries = getAllCountries();
        List<Country> neighbours = allCountries.stream()
                .filter(c -> Objects.nonNull(c.getBorders()))
                .filter(cc -> cc.getBorders().contains(acronym))
                .collect(Collectors.toList());
        // TODO add to detail low configuration
        return neighbours;
    }

    @Override
    public List<Country> getCountriesByLanguage(String language) throws JsonProcessingException {
        String capitalized = Character.toUpperCase(language.charAt(0)) + language.substring(1).toLowerCase();
        List<Country> all = getAllCountries();
        return all.stream()
                .filter(country -> country.getLanguages() != null)
                .filter(country -> country.getLanguages().contains(capitalized))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLanguageByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getLanguages();
    }

    @Override
    public List<String> getTranslationsByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getTranslations();
    }
}
