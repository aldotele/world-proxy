package com.world.worldproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class CityServiceImpl implements CityService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.ninjas.city.base.url}")
    String apiNinjaCityBaseUrl;

    @Value("${api.ninjas.api.key}")
    String apiNinjaApiKey;


    @Override
    public City getCity(String name) throws JsonProcessingException, CityNotFound {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiNinjaApiKey);
        HttpEntity<String> requestEntity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(apiNinjaCityBaseUrl + "?name=" + name,
                HttpMethod.GET,
                requestEntity,
                String.class);

        JSONArray jsonArray = new JSONArray(response.getBody());
        try {
            City city = objectMapper.readValue(jsonArray.get(0).toString(), City.class);
            return city;
        } catch (JSONException e) {
            throw new CityNotFound();
        }
    }
}
