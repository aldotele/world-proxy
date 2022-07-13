package com.world.worldproxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		// stubbing the all countries object of the external service
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
	public void getAllCapitals() throws Exception {
		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		MvcResult result = mockMvc.perform(get("/country/capital/all"))
				.andExpect(status().isOk())
				.andReturn();

		mockServer.verify();

		List<String> capitals = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<String>>() {});

		Assertions.assertTrue(capitals.containsAll(List.of("Rome", "Ottawa", "Tokyo", "Canberra", "Nairobi")));
		Assertions.assertTrue(capitals.size() > 200);
	}

	@Test
	public void getAllCapitalsByContinent() throws Exception {
		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		MvcResult result = mockMvc.perform(get("/country/capital/all")
						.queryParam("continent", "oceania"))
				.andExpect(status().isOk())
				.andReturn();

		mockServer.verify();

		List<String> capitals = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<String>>() {});

		Assertions.assertTrue(capitals.containsAll(List.of("Canberra", "Wellington")));
		Assertions.assertFalse(capitals.contains("Rome"));
		Assertions.assertEquals(27, capitals.size());
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
	public void getCurrencies() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/currency/italy"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currencies").isArray())
				.andExpect(jsonPath("$.currencies.[0]").value("Euro (€)"));

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

	@Test
	public void getCountriesByPopulationRange() throws Exception {
		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.times(4), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		MvcResult result1 = mockMvc.perform(get("/country")
						.queryParam("minPopulation", "10000000")
						.queryParam("maxPopulation", "60000000"))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult result2 = mockMvc.perform(get("/country")
						.queryParam("minPopulation","20000000")
						.queryParam("maxPopulation", "50000000"))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult result3 = mockMvc.perform(get("/country")
						.queryParam("minPopulation","1000000000"))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult result4 = mockMvc.perform(get("/country")
						.queryParam("maxPopulation", "1000000"))
				.andExpect(status().isOk())
				.andReturn();

		mockServer.verify();

		JSONArray resultsBetween10Mand60M = new JSONArray(result1.getResponse().getContentAsString());
		JSONArray resultsBetween20Mand50M = new JSONArray(result2.getResponse().getContentAsString());
		JSONArray resultsWithMin1B = new JSONArray(result3.getResponse().getContentAsString());
		JSONArray resultsWithMax1M = new JSONArray(result4.getResponse().getContentAsString());

		// restricting the population range must result in fewer countries filtered
		Assertions.assertTrue(resultsBetween10Mand60M.length() > resultsBetween20Mand50M.length());

		Assertions.assertTrue(resultsWithMin1B.length() > 0);
		Assertions.assertTrue(resultsWithMax1M.length() > 0);
	}

	@Test
	public void getNeighbours() throws Exception {
		String italy = "get_country_italy.json";
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", italy), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		MvcResult result = mockMvc.perform(get("/country/neighbours/italy"))
				.andExpect(status().isOk())
				.andReturn();

		mockServer.verify();

		JSONArray neighbours = new JSONArray(result.getResponse().getContentAsString());

		Assertions.assertEquals(6, neighbours.length());
	}

	@Test
	public void getZeroNeighbours() throws Exception {
		String malta = "get_country_malta.json";
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", malta), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		MvcResult result = mockMvc.perform(get("/country/neighbours/italy"))
				.andExpect(status().isOk())
				.andReturn();

		mockServer.verify();

		JSONArray neighbours = new JSONArray(result.getResponse().getContentAsString());


		Assertions.assertEquals(0, neighbours.length());
	}

	@Test
	public void getLanguages() throws Exception {
		String malta = "get_country_malta.json";
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", malta), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/malta")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/language/malta"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.languages").isArray())
				.andExpect(jsonPath("$.languages", hasItems("Maltese", "English")));

		mockServer.verify();
	}

	@Test
	public void getTranslations() throws Exception {
		// stubbing the country object of the external service
		String stubbedExternalCountryResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_country_italy.json"), StandardCharsets.ISO_8859_1);

		// stubbing the external request to third party API
		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/name/italy")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalCountryResponse)
				);

		mockMvc.perform(get("/country/translation/italy"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.translations").isArray())
				.andExpect(jsonPath("$.translations", hasItems("Italia", "Italy", "Italie", "Italien", "Taliansko")));

		mockServer.verify();
	}

	@Test
	public void getCountriesByLanguage() throws Exception {
		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.twice(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		// expecting several countries to speak spanish
		mockMvc.perform(get("/country/speak/italian"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[*].name", hasItems("Italy", "Vatican City", "San Marino", "Switzerland")));

		// expecting only Finland to speak finnish
		mockMvc.perform(get("/country/speak/finnish"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[*].name", hasItems("Finland")));

		mockServer.verify();
	}

	@Test
	public void getCountriesByContinent() throws Exception {
		// stubbing the all countries object of the external service
		String stubbedExternalAllCountriesResponse = Files.readString(Paths.get("src", "main", "resources", "stubs", "get_all_countries.json"), StandardCharsets.ISO_8859_1);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI(restCountriesBaseUrl + "/all")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(stubbedExternalAllCountriesResponse));

		mockMvc.perform(get("/country/in/europe"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[*].name", hasItems("Italy", "Germany", "Spain", "Ukraine")));

		mockServer.verify();
	}

}
