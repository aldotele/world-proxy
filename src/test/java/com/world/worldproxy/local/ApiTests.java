package com.world.worldproxy.local;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

/**
 * the purpose of this test class is only testing responses of available city APIs to integrate them in the project
 */
@SpringBootTest
public class ApiTests {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    @Value("${countrycity.base.url}")
    String countryCityBaseUrl;

    @Test
    void postmanCitiesApiTest () {
        String countryToLookFor = "/italy";
        // TODO implement test
    }
}
