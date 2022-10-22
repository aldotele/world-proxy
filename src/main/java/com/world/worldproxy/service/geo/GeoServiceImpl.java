package com.world.worldproxy.service.geo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Lake;
import com.world.worldproxy.model.River;
import com.world.worldproxy.model.response.external.LakeExternalResponse;
import com.world.worldproxy.model.response.external.RiverExternalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class GeoServiceImpl implements GeoService {
    private final String LAKE_CATEGORIES = "350-3500-0304";
    private final String RIVER_CATEGORIES = "350-3500-0302";

    @Value("${api.here.base.url}")
    String apiHereBaseUrl;

    @Value("${api.here.api.key}")
    String apiHereApiKey;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Lake> getLakes(String latitude, String longitude) throws JsonProcessingException {
        String urlForLakes = String.format("%s?at=%s,%s&categories=%s&apiKey=%s",
                apiHereBaseUrl, latitude, longitude, LAKE_CATEGORIES, apiHereApiKey);

        ResponseEntity<String> response = restTemplate.getForEntity(urlForLakes, String.class);
        LakeExternalResponse externalResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        List<Lake> lakes = externalResponse.getItems();

        return lakes;
    }

    @Override
    public List<River> getRivers(String latitude, String longitude) throws JsonProcessingException {
        String urlForLakes = String.format("%s?at=%s,%s&categories=%s&apiKey=%s",
                apiHereBaseUrl, latitude, longitude, RIVER_CATEGORIES, apiHereApiKey);

        ResponseEntity<String> response = restTemplate.getForEntity(urlForLakes, String.class);
        RiverExternalResponse externalResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        List<River> rivers = externalResponse.getItems();
        return rivers;
    }
}
