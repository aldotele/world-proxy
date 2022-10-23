package com.world.worldproxy.service.geo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Lake;
import com.world.worldproxy.model.River;

import java.util.List;

public interface GeoService {
    List<Lake> getLakes(String latitude, String longitude) throws JsonProcessingException;
    List<River> getRivers(String latitude, String longitude) throws JsonProcessingException;
}
