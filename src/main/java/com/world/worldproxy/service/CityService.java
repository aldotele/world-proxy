package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;

public interface CityService {
    City getCity(String name) throws JsonProcessingException, CityNotFound;
}
