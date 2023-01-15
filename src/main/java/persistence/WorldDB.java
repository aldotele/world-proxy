package persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import exception.NotFoundException;
import exception.SearchException;
import lombok.extern.slf4j.Slf4j;
import model.Country;
import model.CountrySearch;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import util.Api;
import util.SimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static configuration.Configuration.*;
import static util.RegexUtil.exactIgnoreCase;

@Slf4j
public class WorldDB {
    static String DB_NAME = "world";
    static MongoClient mongoClient;
    static MongoDatabase database;

    static {
        // when launching docker compose, db host will be mongo, otherwise localhost
        mongoClient = new MongoClient(Optional.ofNullable(ENV.get("MONGO_HOST")).orElse("localhost"), 27017);
        database = mongoClient.getDatabase(DB_NAME);
    }

    public static void init() throws JsonMappingException, JsonProcessingException, IOException {
        // retrieving all countries and writing to mongo
        Request request = SimpleClient.buildRequest(Api.External.REST_COUNTRIES_BASE_URL + "/all");
        Response response = SimpleClient.makeRequest(request);
        List<Country> allCountries = Arrays.asList(mapper.readValue(Objects.requireNonNull(response.body()).string(), Country[].class));
        WorldDB.createCollection("countries");
        WorldDB.writeManyToCollection("countries", allCountries);

        // retrieving countries coordinates and updating mongo documents
        URL data = new URL(Api.External.COUNTRY_CODES_COORDINATES_CSV);
        BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));
        String inputLine;

        int lineNumber = 0;
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
        }
        catch (Exception e) {
            log.error("Failed at creating collection " + collectionName);
            log.error(e.getMessage());
        }
    }

    public static List<Country> retrieveAll(String collectionName) throws JsonProcessingException {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        ArrayList<String> found = collection.find().projection(Projections.excludeId())
                .map(Document::toJson)
                .into(new ArrayList<>());
        List<Country> allCountries = simpleMapper.readValue(found.toString(), new TypeReference<>(){});
        return allCountries;
    }

    public static Country retrieveCountry(String countryName) throws JsonProcessingException, NotFoundException {
        MongoCollection<Document> collection = database.getCollection("countries");
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
     * @param search
     * @return list of countries
     * @throws JsonProcessingException
     */
    public static List<Country> retrieveCountries(CountrySearch search) throws JsonProcessingException, SearchException {
        MongoCollection<Document> collection = database.getCollection("countries");
        BasicDBList conditions = buildQueryConditions(search);

        String queryResult = collection.find(new BasicDBObject("$and", conditions))
                .projection(Projections.excludeId())
                .map(Document::toJson)
                .into(new ArrayList<>())
                .toString();

        List<Country> allCountries = simpleMapper.readValue(queryResult, new TypeReference<>(){});

        return allCountries;
    }

    public static List<String> retrieveCountriesAcronyms(CountrySearch search) throws JsonProcessingException, SearchException {
        MongoCollection<Document> collection = database.getCollection("countries");
        BasicDBList conditions = buildQueryConditions(search);

        List<String> filtered = collection
                .distinct("acronym", String.class)
                .filter(new BasicDBObject("$and", conditions))
                .into(new ArrayList<>());

        return filtered;
    }

    @NotNull
    private static BasicDBList buildQueryConditions(CountrySearch search) throws SearchException {
        BasicDBList conditions = new BasicDBList();

        // population range
        BigDecimal maxPopulation = search.getMaxPopulation();
        if (maxPopulation != null) conditions.add(new BasicDBObject("population", new BasicDBObject("$lt", maxPopulation)));
        BigDecimal minPopulation = search.getMinPopulation();
        if (minPopulation != null) conditions.add(new BasicDBObject("population", new BasicDBObject("$gt", minPopulation)));

        // spoken languages
        List<String> languages = search.getLanguages();
        if (languages != null) {
            for (String language: languages) {
                conditions.add(new BasicDBObject("languages", exactIgnoreCase(language)));
            }
        }

        // borders
        List<String> borders = search.getBorders();
        if (borders != null) {
            for (String border: borders) conditions.add(new BasicDBObject("borders", border));
        }

        // name regex
        String nameRegex = search.getNameRegex();
        if (nameRegex != null) {
            Pattern namePattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
            conditions.add(new BasicDBObject("name", new BasicDBObject("$regex", namePattern)));
        }

        // capital regex
        String capitalRegex = search.getCapitalRegex();
        if (capitalRegex != null) {
            Pattern capitalPattern = Pattern.compile(capitalRegex, Pattern.CASE_INSENSITIVE);
            conditions.add(new BasicDBObject("capital", new BasicDBObject("$regex", capitalPattern)));
        }

        if (conditions.isEmpty()) {
            throw new SearchException("at least one valid search criteria is needed.");
        }
        return conditions;
    }

    public static String retrieveEnglishCountryName(String countryName) {
        MongoCollection<Document> collection = database.getCollection("countries");
        String countryNameCapitalized = countryName.substring(0, 1).toUpperCase() +
                countryName.substring(1).toLowerCase();
        Document document = collection.find(new BasicDBObject("translations", countryNameCapitalized)).first();
        if (document != null) {
            return document.get("name").toString();
        }
        return null;
    }

    public static List<String> retrieveLanguages(String collectionName) {
        MongoCollection<Document> collection = database.getCollection("countries");
        return collection.distinct("languages", String.class)
                .into(new ArrayList<>());
    }

    public static List<String> retrieveCurrencies(String collectionName) {
        MongoCollection<Document> collection = database.getCollection("countries");
        return collection.distinct("currencies", String.class)
                .into(new ArrayList<>());
    }

    public static List<String> retrieveCapitals(String countries) {
        MongoCollection<Document> collection = database.getCollection("countries");
        return collection.distinct("capital", String.class)
                .into(new ArrayList<>());
    }
}
