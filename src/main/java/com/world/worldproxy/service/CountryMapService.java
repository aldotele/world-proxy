package com.world.worldproxy.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
public class CountryMapService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    public String getMapsByCountryName(String country) {
        ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/name/" + country, String.class);
        JSONArray jsonArr = new JSONArray(response.getBody());
        JSONObject jsonObj = (JSONObject) jsonArr.get(0);
        return (String) jsonObj.getJSONObject("maps").toMap().get("googleMaps");
    }
}
