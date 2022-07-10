package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.service.country.CountryService;
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
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isA;
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

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withStatus(HttpStatus.OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(stubbedExternalAllCountriesResponse));

		mockMvc.perform(get("/country/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", isA(ArrayList.class)))
			.andExpect(jsonPath("$.[*].name", hasItems("Italy", "Colombia", "Ghana", "China", "Australia")))
			.andExpect(jsonPath("$.[*].capital", hasItems("Mexico City", "Moscow", "Wellington", "Beijing")))
			.andExpect(jsonPath("$.[*].acronym", hasItems("BLM", "MCO", "NPL", "BGD")))
			.andExpect(jsonPath("$.[0].name", isA(String.class)))
			.andExpect(jsonPath("$.[0].capital", isA(String.class)))
			.andExpect(jsonPath("$.[0].maps", isA(String.class)))
			.andExpect(jsonPath("$.[0].flag", isA(String.class)))
			.andExpect(jsonPath("$.[0].population", isA(Integer.class)))
			.andExpect(jsonPath("$.[0].currencies", isA(ArrayList.class)))
			.andExpect(jsonPath("$.[0].continents", isA(ArrayList.class)))
			.andExpect(jsonPath("$.[0].translations", isA(ArrayList.class)))
			.andReturn();

		mockServer.verify();
	}

	@Test
	public void getMaps() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/maps/italy"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.maps").value("https://goo.gl/maps/8M1K27TDj7StTRTq8"));

		mockServer.verify();
	}

	@Test
	public void getCapital() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/capital/italy"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.capital").value("Rome"));

		mockServer.verify();
	}

	@Test
	public void getFlag() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/flag/italy"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.flag").value("https://flagcdn.com/it.svg"));

		mockServer.verify();
	}

}
