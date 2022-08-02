package com.world.worldproxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.entity.CountryTranslation;
import com.world.worldproxy.repository.CountryTranslationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ComponentScan("com.world.worldproxy.config")
@WebMvcTest
@ActiveProfiles("multilingual")
public class WebMultilingualTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CountryTranslationRepository countryTranslationRepository;

    private MockRestServiceServer mockServer;

    @Value("${restcountries.base.url}")
    String restCountriesBaseUrl;

    @Value("${countrycity.base.url}")
    String countryCityBaseUrl;

    @BeforeEach
    public void createServer() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }


    @Test
    public void getCountry() throws Exception {
        // stubbing the country object of the external service
        String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "test", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

        Mockito.when(countryTranslationRepository.findByTranslation("taliansko"))
                .thenReturn(List.of(new CountryTranslation("italy", "italy")));

        // stubbing the external request to third party API
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(stubbedExternalCountryResponse)
                );

        mockMvc.perform(get("/country/taliansko"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Italy"))
                .andExpect(jsonPath("$.officialName").value("Italian Republic"))
                .andExpect(jsonPath("$.acronym").value("ITA"))
                .andExpect(jsonPath("$.capital").value("Rome"))
                .andExpect(jsonPath("$.borders.length()").value(6))
                .andExpect(jsonPath("$.maps").value( "https://goo.gl/maps/8M1K27TDj7StTRTq8"))
                .andExpect(jsonPath("$.currencies.length()").value(1))
                .andExpect(jsonPath("$.currencies[0]").value("Euro (€)"))
                .andExpect(jsonPath("$.population").isNumber())
                .andExpect(jsonPath("$.continents.length()").value(1))
                .andExpect(jsonPath("$.continents[0]").value("Europe"))
                .andExpect(jsonPath("$.flag").value("https://flagcdn.com/it.svg"))
                .andExpect(jsonPath("$.languages.length()").value(1))
                .andExpect(jsonPath("$.languages[0]").value("Italian"))
                .andExpect(jsonPath("$.translations").isArray())
                .andReturn();

        mockServer.verify();
    }

    @Test
    public void getAllCitiesByCountry() throws Exception {
        // stubbing the all cities object of the external service
        String stubbedExternalCitiesResponse = Files.readString(Paths.get("src", "test", "resources", "stubs", "get_all_cities_italy.json"), StandardCharsets.ISO_8859_1);

        Mockito.when(countryTranslationRepository.findByTranslation("taliansko"))
                .thenReturn(List.of(new CountryTranslation("taliansko", "italy")));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(countryCityBaseUrl + "/q?country=italy")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(stubbedExternalCitiesResponse));

        MvcResult result = mockMvc.perform(get("/city/in/taliansko"))
                .andExpect(status().isOk())
                .andReturn();

        mockServer.verify();

        List<String> cities = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        // some major cities
        Assertions.assertTrue(cities.containsAll(List.of("Milan", "Rome", "Turin", "Naples", "Bologna", "Florence")));
        // some minor cities
        Assertions.assertTrue(cities.containsAll(List.of("Alessandria", "Biella", "Caltanissetta", "Potenza", "Terni")));
        // some small towns
        Assertions.assertTrue(cities.containsAll(List.of("Santa Maria a Vico", "Grandate", "Monopoli", "Frascati")));
    }
}
