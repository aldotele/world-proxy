package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;

import java.math.BigDecimal;
import java.util.List;

public interface CountryService {
    List<String> getAllCountries() throws JsonProcessingException;
    List<Country> getAllCountriesDetail() throws JsonProcessingException;
    Country getCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    String getMapsByCountry(String countryName) throws CountryNotFoundException, JsonProcessingException;
    List<String> getAllCapitals(String continentName) throws JsonProcessingException;
    String getCapitalByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    List<String> getCurrencyByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    String getFlagByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    <T> List<T> getCountriesByPopulationRange(BigDecimal minimum, BigDecimal maximum) throws JsonProcessingException, QueryParameterException;
    <T> List<T> getCountryNeighbours(String countryName) throws JsonProcessingException, CountryNotFoundException;
    <T> List<T> getCountriesByLanguage(String language) throws JsonProcessingException;
    <T> List<T> getCountriesByContinent(String continentName) throws JsonProcessingException;
    List<String> getLanguageByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    List<String> getTranslationsByCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
    <T> List<T> getCountriesMultilingual() throws JsonProcessingException;
}
