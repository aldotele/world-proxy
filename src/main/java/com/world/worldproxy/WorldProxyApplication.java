package com.world.worldproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.worldproxy.entity.CountryTranslation;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.repository.CountryTranslationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class WorldProxyApplication {

	public static final String MULTILINGUAL = "multilingual";

	public static void main(String[] args) {
		SpringApplication.run(WorldProxyApplication.class, args);
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


@Component
@Profile(value = "multilingual")
@Slf4j
class MultilingualRunner implements CommandLineRunner {

	@Value("${restcountries.base.url}")
	String restCountriesBaseUrl;

	@Value("${spring.datasource.url}")
	String datasourceUrl;

	@Value("${spring.datasource.username}")
	String datasourceUsername;

	@Value("${spring.datasource.password}")
	String datasourcePassword;

	@Autowired
	CountryTranslationRepository countryTranslationRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		Connection connection = DriverManager.getConnection(datasourceUrl, datasourceUsername, datasourcePassword);

		String countryTranslationTable = "country_translation";
		if (!isTablePresentSQL(connection, countryTranslationTable)) {
			log.info("SQL statement: creating " + countryTranslationTable + " table ...");
			createTableSQL(connection, countryTranslationTable);
		}

		// TODO find smarter way to check if table is filled with data
		List<CountryTranslation> data = countryTranslationRepository.findAll();
		if (data.size() == 0) {
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
	}

	private boolean isTablePresentSQL(Connection connection, String tableName) throws SQLException {
		String query = "SELECT count(*) FROM information_schema.tables WHERE table_name = ? LIMIT 1;";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, tableName);
		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		boolean isPresent = resultSet.getInt(1) != 0;
		return isPresent;
	}

	private void createTableSQL(Connection connection, String tableName) throws SQLException {
		// TODO replace hardcoded table name with placeholder
		String query = "CREATE table country_translation (id int auto_increment, country varchar(255), translation varchar(255), primary key(id));";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
//		preparedStatement.setString(1, tableName);
		preparedStatement.executeUpdate();
	}
}

