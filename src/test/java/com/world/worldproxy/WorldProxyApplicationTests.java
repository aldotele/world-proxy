package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.controller.CountryController;
import com.world.worldproxy.service.CountryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CountryController.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan({"com.world.worldproxy.service", "com.world.worldproxy.config"})
class WorldProxyApplicationTests {

	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private CountryService countryService;

	@Mock
	private RestTemplate restTemplate;

	@Value("${restcountries.base.url}")
	String restCountriesBaseUrl;


	@Test
	void contextLoads() {
	}


	@Test
	public void getCountry() throws Exception {
		String stubbedJsonResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		Mockito.lenient().when(restTemplate.getForEntity(restCountriesBaseUrl + "/italy", String.class))
				.thenReturn(new ResponseEntity<>(stubbedJsonResponse, HttpStatus.OK));

		mockmvc.perform(MockMvcRequestBuilders
						.get("/country/italy")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Italy"))
				.andExpect(jsonPath("$.acronym").value("ITA"))
				.andExpect(jsonPath("$.capital").value("Rome"))
				.andExpect(jsonPath("$.languages[0]").value("Italian"))
				.andExpect(jsonPath("$.continents[0]").value("Europe"))
				.andExpect(jsonPath("$..translations[*]").isNotEmpty())
				.andExpect(jsonPath("$.population").isNumber())
				.andReturn();
	}

	@Test
	public void getAllCountries() throws Exception {

	}

}
