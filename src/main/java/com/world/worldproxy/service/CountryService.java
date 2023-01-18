package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.NotFoundException;
import com.world.worldproxy.model.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries() throws JsonProcessingException;
    Country getCountry(String countryName) throws JsonProcessingException, NotFoundException;
}
