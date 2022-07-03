package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.config.WorldProxyConfiguration;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.service.country.CountryService;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
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


@ComponentScan("com.world.worldproxy.config")
//@ActiveProfiles(profiles = "shallow")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WorldProxyConfiguration.class)
class WorldProxyApplicationTests {

	@Autowired
	private RestTemplate restTemplate;

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

		// building the expected response when calling service
		Country expectedCountryResponse = objectMapper.readValue(stubbedExternalCountryObject.get(0).toString(), Country.class);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		// storing the actual response
		Country actualCountryResponse = countryService.getCountry("italy");
		mockServer.verify();

		Assertions.assertEquals(expectedCountryResponse, actualCountryResponse);
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
