package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;

import java.math.BigDecimal;
import java.util.List;

public interface CountryService {
    List<Country> getAllCountries() throws JsonProcessingException;
    Country getCountry(String name) throws JsonProcessingException;
    String getMapsByCountry(String country);
    List<String> getAllCapitals(String continent) throws JsonProcessingException;
    String getCapitalByCountry(String country) throws  JsonProcessingException;
    List<String> getCurrencyByCountry(String country) throws  JsonProcessingException;
    String getFlagByCountry(String country) throws JsonProcessingException;
    <T> List<T> getCountriesByPopulationRange(BigDecimal minimum, BigDecimal maximum) throws JsonProcessingException, QueryParameterException;
    <T> List<T> getCountryNeighbours(String country) throws JsonProcessingException;
    <T> List<T> getCountriesByLanguage(String language) throws JsonProcessingException;
    <T> List<T> getCountriesByContinent(String continent) throws JsonProcessingException;
    List<String> getLanguageByCountry(String country) throws JsonProcessingException;
    List<String> getTranslationsByCountry(String country) throws JsonProcessingException;
}
