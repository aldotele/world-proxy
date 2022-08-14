package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;
import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    public List<String> getAllCountries() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
        List<Country> countries = Arrays.asList(objectMapper.readValue(response.getBody(), Country[].class));
        return countries.stream()
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<Country> getAllCountriesDetail() throws JsonProcessingException {
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

    @Override
    public String getMapsByCountry(String countryName) throws CountryNotFoundException, JsonProcessingException {
        return getCountry(countryName).getMaps();
    }

    @Override
    public List<String> getAllCapitals(String continentName) throws JsonProcessingException {
        return continentName != null ?

                getAllCountriesDetail().stream()
                        .filter(country -> country.getContinents().contains(WordUtils.capitalizeFully(continentName)))
                        .map(Country::getCapital)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :

                getAllCountriesDetail().stream()
                        .map(Country::getCapital)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    @Override
    public String getCapitalByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        return getCountry(countryName).getCapital();
    }

    @Override
    public List<String> getCurrencyByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        return getCountry(countryName).getCurrencies();
    }

    @Override
    public String getFlagByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        return getCountry(countryName).getFlag();
    }

    @Override
    public List<String> getCountriesByContinent(String continentName) throws JsonProcessingException {
        return getAllCountriesDetail().stream()
                .filter(country -> country.getContinents().contains(WordUtils.capitalizeFully(continentName)))
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    @Override public List<String> getCountriesByPopulationRange(BigDecimal minimum, BigDecimal maximum) throws JsonProcessingException, QueryParameterException {
        List<Country> allCountries = getAllCountriesDetail();
        // both min and max provided
        if (minimum != null & maximum != null) {
            if (minimum.compareTo(maximum) > 0) {
                throw new QueryParameterException("population range is not valid.");
            }
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(minimum) > 0 & country.getPopulation().compareTo(maximum) < 0)
                    .map(Country::getName)
                    .collect(Collectors.toList());
        }
        // only min provided
        else if (minimum != null & maximum == null) {
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(minimum) > 0)
                    .map(Country::getName)
                    .collect(Collectors.toList());
        }
        // only max provided
        else if (maximum != null) {
            return allCountries.stream()
                    .filter(country -> country.getPopulation().compareTo(maximum) < 0)
                    .map(Country::getName)
                    .collect(Collectors.toList());
        }
        else {
            throw new QueryParameterException("at least one parameter between minPopulation and maxPopulation is required.");
        }
    }

    @Override
    public List<String> getCountryNeighbours(String countryName) throws JsonProcessingException, CountryNotFoundException {
        String acronym = getCountry(countryName).getAcronym();
        List<Country> allCountries = getAllCountriesDetail();
        return allCountries.stream()
                .filter(c -> Objects.nonNull(c.getBorders()))
                .filter(cc -> cc.getBorders().contains(acronym))
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCountriesByLanguage(String language) throws JsonProcessingException {
        String capitalized = Character.toUpperCase(language.charAt(0)) + language.substring(1).toLowerCase();
        List<Country> allCountries = getAllCountriesDetail();
        return allCountries.stream()
                .filter(country -> country.getLanguages() != null)
                .filter(country -> country.getLanguages().contains(capitalized))
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLanguageByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        return getCountry(countryName).getLanguages();
    }

    @Override
    public List<String> getTranslationsByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException {
        return getCountry(countryName).getTranslations();
    }

    @Override
    public List<String> getCountriesMultilingual() throws JsonProcessingException {
        return getAllCountriesDetail().stream()
                .filter(country -> country.getLanguages() != null)
                .filter(country -> country.getLanguages().size() > 1)
                .map(Country::getName)
                .collect(Collectors.toList());
    }

}
