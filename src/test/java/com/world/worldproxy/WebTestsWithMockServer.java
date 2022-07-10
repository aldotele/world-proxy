package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.service.country.CountryService;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ComponentScan("com.world.worldproxy.config")
//@ActiveProfiles(profiles = "shallow")
@WebMvcTest
class WebTestsWithMockServer {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	CountryService countryService;

	@Autowired
	private ObjectMapper objectMapper;

	private MockRestServiceServer mockServer;

	@Value("${restcountries.base.url}")
	String restCountriesBaseUrl;

	@BeforeEach
	public void createServer() throws Exception {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}


	@Test
	public void getCountry() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);
		JSONArray stubbedExternalCountryObject = new JSONArray(stubbedExternalCountryResponse);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/italy"))
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
	public void getAllCountries() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);
		JSONArray stubbedExternalAllCountriesObject = new JSONArray(stubbedExternalAllCountriesResponse);

		// building the expected response when calling service
		List<Country> expectedAllCountriesResponse = Arrays.asList(objectMapper.readValue(stubbedExternalAllCountriesResponse, Country[].class));

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		// storing the actual response
		List<Country> actualAllCountriesResponse = countryService.getAllCountries();
		mockServer.verify();

		Assertions.assertEquals(expectedAllCountriesResponse, actualAllCountriesResponse);
	}

}
