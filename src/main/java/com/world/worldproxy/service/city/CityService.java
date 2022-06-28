package com.world.worldproxy.service.city;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;
import com.world.worldproxy.model.response.CountryCitiesResponse;

public interface CityService {
    City getCityData(String name) throws JsonProcessingException, CityNotFound;
    CountryCitiesResponse getCities(String country) throws JsonProcessingException;
}
