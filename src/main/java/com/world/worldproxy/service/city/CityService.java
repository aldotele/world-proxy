package com.world.worldproxy.service.city;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;

import java.util.List;

public interface CityService {
    City getCityData(String name) throws JsonProcessingException, CityNotFound;
    List<String> getCities(String country, String startWith) throws JsonProcessingException;
}
