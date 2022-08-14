package com.world.worldproxy.service.city;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CityNotFoundException;
import com.world.worldproxy.model.City;

import java.util.List;

public interface CityService {
    City getCityDetails(String name) throws JsonProcessingException, CityNotFoundException;
    List<String> getCities(String country, String startWith) throws JsonProcessingException;
}
