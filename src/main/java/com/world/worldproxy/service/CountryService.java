package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;

import java.math.BigDecimal;
import java.util.List;

public interface CountryService {
    List<Country> getAllCountries() throws JsonProcessingException;
    Country getCountry(String name) throws JsonProcessingException;
    String getMapsByCountry(String country);
    String getCapitalByCountry(String country) throws  JsonProcessingException;
    String getFlagByCountry(String country) throws JsonProcessingException;
    List<Country> getCountriesByPopulationRange(BigDecimal minPopulation, BigDecimal maxPopulation) throws JsonProcessingException;
    List<String> getCountryNeighbours(String country) throws JsonProcessingException;
}
