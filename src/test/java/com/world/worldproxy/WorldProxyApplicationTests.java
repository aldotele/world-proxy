package com.world.worldproxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.controller.CountryController;
import com.world.worldproxy.model.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CountryController.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan({"com.world.worldproxy.service", "com.world.worldproxy.config"})
@ActiveProfiles(profiles = "shallow")
class WorldProxyApplicationTests {

	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private RestTemplate restTemplate;

	@Value("${restcountries.base.url}")
	String restCountriesBaseUrl;

	@InjectMocks
	CountryController countryController;


	@Test
	void contextLoads() {
	}


	@Test
	public void getCountry() throws Exception {
		String stubbedJsonResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		Mockito.lenient().when(restTemplate.getForEntity(restCountriesBaseUrl + "/name/italy", String.class))
				.thenReturn(new ResponseEntity<>(stubbedJsonResponse, HttpStatus.OK));

		mockmvc.perform(MockMvcRequestBuilders
						.get("/country/italy")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Italy"))
				.andExpect(jsonPath("officialName").value("Italian Republic"))
				.andExpect(jsonPath("$.acronym").value("ITA"))
				.andExpect(jsonPath("$.capital").value("Rome"))
				.andExpect(jsonPath("$.languages[0]").value("Italian"))
				.andExpect(jsonPath("$.continents[0]").value("Europe"))
				.andExpect(jsonPath("$..translations[*]").isNotEmpty())
				.andExpect(jsonPath("$.population").isNumber());

		Mockito.verify(restTemplate, Mockito.times(1))
				.getForEntity(restCountriesBaseUrl + "/name/italy", String.class);
	}

	@Test
	@Disabled
	public void getAllCountries() throws Exception {
		String stubbedJsonResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);
		Mockito.when(restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class))
				.thenReturn(new ResponseEntity<>(stubbedJsonResponse, HttpStatus.OK));

		MvcResult result = mockmvc.perform(MockMvcRequestBuilders
				.get("/country/all")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();
		List<Country> countriesResponse =  objectMapper.readValue(response, new TypeReference<List<Country>>() {});

		Mockito.verify(restTemplate, Mockito.times(1))
				.getForEntity(restCountriesBaseUrl + "/all", String.class);

		Assertions.assertEquals(3, countriesResponse.size());
	}

}
