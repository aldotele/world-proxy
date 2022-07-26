package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.repository.CountryTranslationRepository;
import com.world.worldproxy.service.multilingual.LanguageNormalizer;
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

    @Autowired
    CountryTranslationRepository countryTranslationRepository;

    @Autowired
    LanguageNormalizer languageNormalizer;

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
    public Country getCountry(String name) throws JsonProcessingException {
        String englishName = languageNormalizer.normalizeToEnglish(name);
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + englishName, String.class);
        JSONArray jsonArray = new JSONArray(response.getBody());
        Country country = objectMapper.readValue(jsonArray.get(0).toString(), Country.class);
        return country;
    }

    @Override
    public String getMapsByCountry(String country) {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + country, String.class);
        JSONArray jsonArr = new JSONArray(response.getBody());
        JSONObject jsonObj = (JSONObject) jsonArr.get(0);
        return (String) jsonObj.getJSONObject("maps").get("googleMaps");
    }

    @Override
    public List<String> getAllCapitals(String continent) throws JsonProcessingException {
        return continent != null ?

                getAllCountriesDetail().stream()
                        .filter(country -> country.getContinents().contains(WordUtils.capitalizeFully(continent)))
                        .map(Country::getCapital)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :

                getAllCountriesDetail().stream()
                        .map(Country::getCapital)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
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
    public List<String> getCountriesByContinent(String continent) throws JsonProcessingException {
        return getAllCountriesDetail().stream()
                .filter(country -> country.getContinents().contains(WordUtils.capitalizeFully(continent)))
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
    public List<String> getCountryNeighbours(String country) throws JsonProcessingException {
        String acronym = getCountry(country).getAcronym();
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
    public List<String> getLanguageByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getLanguages();
    }

    @Override
    public List<String> getTranslationsByCountry(String country) throws JsonProcessingException {
        return getCountry(country).getTranslations();
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
