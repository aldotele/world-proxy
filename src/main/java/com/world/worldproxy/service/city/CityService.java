package com.world.worldproxy.service.city;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CityNotFoundException;
import com.world.worldproxy.model.City;

import java.util.List;

public interface CityService {
    City getCityDetails(String cityName) throws JsonProcessingException, CityNotFoundException;
    List<String> getCities(String countryName, String startWith) throws JsonProcessingException;
}
