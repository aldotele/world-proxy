package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries() throws JsonProcessingException;
    Country getCountryByName(String name) throws JsonProcessingException;
    String getMapsByCountryName(String country);
}
