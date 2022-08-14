package com.world.worldproxy.service.city;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.exception.CityNotFoundException;
import com.world.worldproxy.model.City;
import com.world.worldproxy.model.request.ApiKeyRequest;
import com.world.worldproxy.model.response.external.CountryCitiesExternalResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


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

    @Value("${countrycity.base.url}")
    String countryCityBaseUrl;


    @Override
    public List<String> getCities(String country, String startWith) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(countryCityBaseUrl + "/" + "q?country=" + country, String.class);
        CountryCitiesExternalResponse externalResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        List<String> allCountryCities = externalResponse.getData();
        return startWith != null ? allCountryCities.stream()
                .filter(city -> city.toLowerCase().startsWith(startWith.toLowerCase()))
                .collect(Collectors.toList()) : allCountryCities;
    }

    @Override
    public City getCityDetails(String name) throws JsonProcessingException, CityNotFoundException {
        HttpEntity<String> apiKeyRequest = ApiKeyRequest.buildRequest(apiNinjaApiKey);

        ResponseEntity<String> response = restTemplate.exchange(apiNinjaCityBaseUrl + "?name=" + name,
                HttpMethod.GET,
                apiKeyRequest,
                String.class);

        JSONArray jsonArray = new JSONArray(response.getBody());
        if (jsonArray.length() > 0) {
            City city = objectMapper.readValue(jsonArray.get(0).toString(), City.class);
            return city;
        } else {
            throw new CityNotFoundException(name);
        }
    }
}
