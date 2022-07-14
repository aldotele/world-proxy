package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.entity.CountryTranslation;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.repository.CountryTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class WorldProxyApplication implements CommandLineRunner {

	@Value("${restcountries.base.url}")
	String restCountriesBaseUrl;

	@Autowired
	CountryTranslationRepository countryTranslationRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(WorldProxyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// saving on database all translations for each country
		// this is done to support multilingual country queries
		// e.g. queries such as "deutschland" and "germania" will be standardized to "germany" after looking on db
		ResponseEntity<String> response = restTemplate.getForEntity(restCountriesBaseUrl + "/all", String.class);
		List<Country> allCountries = Arrays.asList(objectMapper.readValue(response.getBody(), Country[].class));

		// for each country, each translation will be extracted and saved in a row
		// example of a database row will be translation=italia, country=italy
		allCountries.forEach(country -> country.getTranslations().stream().distinct()
				.forEach(translation -> countryTranslationRepository.save(
						new CountryTranslation(translation, country.getName())
				))
		);
	}

	@RestController
	@RequestMapping(path = "")
	public static class WelcomeController {

		@GetMapping("")
		String welcome() {
			return "Welcome to World Proxy service";
		}
	}

}

