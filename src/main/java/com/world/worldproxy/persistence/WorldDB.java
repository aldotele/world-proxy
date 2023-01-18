package com.world.worldproxy.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import com.world.worldproxy.exception.NotFoundException;
import com.world.worldproxy.exception.SearchException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.model.CountrySearch;
import com.world.worldproxy.persistence.util.QueryBuilder;
import com.world.worldproxy.util.Api;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.world.worldproxy.config.Configuration.restTemplate;
import static com.world.worldproxy.config.Configuration.simpleMapper;

@Slf4j
public class WorldDB {
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    static String DB_NAME = "world";
    static MongoClient mongoClient;
    static MongoDatabase database;

    private static class Collection {
        private static final String COUNTRIES = "countries";
    }

    public void init() throws JsonMappingException, JsonProcessingException, IOException {
        // when launching docker compose, db host will be mongo, otherwise localhost
        mongoClient = new MongoClient(Optional.ofNullable(Dotenv.load().get("MONGO_HOST")).orElse("localhost"), 27017);
        database = mongoClient.getDatabase(DB_NAME);

        // retrieving all countries and writing to mongo
        ResponseEntity<String> response = restTemplate.getForEntity(Api.External.REST_COUNTRIES_BASE_URL + "/all", String.class);
        List<Country> allCountries = Arrays.asList(mapper.readValue(response.getBody(), Country[].class));
        WorldDB.createCollection("countries");
        WorldDB.writeManyToCollection("countries", allCountries);

        // retrieving countries coordinates and updating mongo documents
        URL data = new URL(Api.External.COUNTRY_CODES_COORDINATES_CSV);
        BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));
        String inputLine;

        int lineNumber = 0;

        // TODO refactor avoiding while loop
        while ((inputLine = in.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1) continue;  // skipping header
            String[] lineSplit = inputLine.split("\", ");
            String isoCode = lineSplit[1].replace('"', ' ').trim();
            String latitude = lineSplit[4].replace('"', ' ').trim();
            String longitude = lineSplit[5].replace('"', ' ').trim();

            BasicDBList coordinates = new BasicDBList();
            coordinates.add(longitude);
            coordinates.add(latitude);

            BasicDBObject location = new BasicDBObject("type", "Point");
            location.put("coordinates", coordinates);
            BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("location", location));

            MongoCollection<Document> collection = database.getCollection("countries");
            UpdateResult updateResult = collection.updateOne(new BasicDBObject("isoCode", isoCode), set);
        }
        in.close();
    }

    public static <T> void writeManyToCollection(String collectionName, List<T> objects) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documents = new ArrayList<>();
        Gson gson = new Gson();
        objects.forEach(obj -> {
            documents.add(Document.parse(gson.toJson(obj)));
            // ALTERNATIVE USING OBJECT MAPPER
            /*
            try {
                documents.add(Document.parse(objectMapper.writeValueAsString(country)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            */
        });
        collection.insertMany(documents);
    }

    public static void createCollection(String collectionName) {
        try {
            // if the collection exists, clean it to start fresh
            database.getCollection(collectionName).drop();
            database.createCollection(collectionName);
            log.info("Collection " + collectionName + " created successfully");
        } catch (Exception e) {
            log.error("Failed at creating collection " + collectionName);
            log.error(e.getMessage());
        }
    }

    /**
     * retrieves all world countries
     */
    public static List<Country> retrieveCountries() throws JsonProcessingException {
        MongoCollection<Document> collection = database.getCollection(Collection.COUNTRIES);
        ArrayList<String> found = collection.find().projection(Projections.excludeId())
                .map(Document::toJson)
                .into(new ArrayList<>());
        List<Country> allCountries = simpleMapper.readValue(found.toString(), new TypeReference<>() {
        });
        return allCountries;
    }

    /**
     * retrieves a country by name
     */
    public static Country retrieveCountry(String countryName) throws JsonProcessingException, NotFoundException {
        MongoCollection<Document> collection = database.getCollection(Collection.COUNTRIES);
        String countryNameCapitalized = countryName.substring(0, 1).toUpperCase() +
                countryName.substring(1).toLowerCase();
        // retrieving the country from whatever language
        // TODO handle case of name with multiple words
        Document document = collection.find(new BasicDBObject("translations", countryNameCapitalized))
                .projection(Projections.excludeId()).first();
        if (document != null) {
            Country country = simpleMapper.readValue(document.toJson(), Country.class);
            return country;
        }
        throw new NotFoundException(countryName);
    }

    /**
     * retrieves all countries that match the filter criteria
     */
    public static List<Country> retrieveCountries(CountrySearch search) throws JsonProcessingException, SearchException {
        MongoCollection<Document> collection = database.getCollection(Collection.COUNTRIES);
        BasicDBList conditions = QueryBuilder.buildQueryConditions(search);

        String queryResult = collection.find(new BasicDBObject("$and", conditions))
                .projection(Projections.excludeId())
                .map(Document::toJson)
                .into(new ArrayList<>())
                .toString();

        List<Country> allCountries = simpleMapper.readValue(queryResult, new TypeReference<>() {
        });

        return allCountries;
    }

    /**
     * retrieves the list of iso2 codes of the countries that match the filter criteria
     */
    public static List<String> retrieveCountriesAcronyms(CountrySearch search) throws SearchException {
        MongoCollection<Document> collection = database.getCollection(Collection.COUNTRIES);
        BasicDBList conditions = QueryBuilder.buildQueryConditions(search);

        List<String> filtered = collection
                .distinct("acronym", String.class)
                .filter(new BasicDBObject("$and", conditions))
                .into(new ArrayList<>());

        return filtered;
    }

    /**
     * retrieves the english country name
     */
    public static String retrieveEnglishCountryName(String countryName) {
        MongoCollection<Document> collection = database.getCollection(Collection.COUNTRIES);
        String countryNameCapitalized = countryName.substring(0, 1).toUpperCase() +
                countryName.substring(1).toLowerCase();
        Document document = collection.find(new BasicDBObject("translations", countryNameCapitalized)).first();
        if (document != null) {
            return document.get("name").toString();
        }
        return null;
    }
}

