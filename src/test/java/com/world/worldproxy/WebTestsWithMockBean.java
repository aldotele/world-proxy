package com.world.worldproxy;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.service.country.CountryService;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ComponentScan("com.world.worldproxy.config")
public class WebTestsWithMockBean {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Country expectedCountryResponse;

    @BeforeEach
    void setUp() throws IOException, JSONException {
        String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);
        JSONArray stubbedExternalCountryObject = new JSONArray(stubbedExternalCountryResponse);
        expectedCountryResponse = objectMapper.readValue(stubbedExternalCountryObject.get(0).toString(), Country.class);
    }

    @Test
    public void getCountry() throws Exception {

        // stubbing the external country response of third party API
        when(countryService.getCountry("italy")).thenReturn(expectedCountryResponse);

        this.mockMvc
                .perform(get("/country/italy"))
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
                .andExpect(jsonPath("$.translations").isArray());

        Mockito.verify(countryService, times(1)).getCountry("italy");
    }


}
