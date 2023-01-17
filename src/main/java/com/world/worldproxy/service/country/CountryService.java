package com.world.worldproxy.service.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.model.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries() throws JsonProcessingException;
    Country getCountry(String countryName) throws JsonProcessingException, CountryNotFoundException;
}
