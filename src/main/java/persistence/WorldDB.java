package persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Country;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static configuration.Configuration.simpleMapper;

@Slf4j
public class WorldDB {
    static String DB_NAME = "world";
    static MongoClient mongoClient;
    static MongoDatabase database;

    static {
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase(DB_NAME);
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

    public static List<Country> retrieveCountriesByPopulationRange(Integer minPopulation, Integer maxPopulation) throws JsonProcessingException {
        MongoCollection<Document> collection = database.getCollection("countries");
        // db.countries.find({$and: [{"population": {$gt: 20000000}}, {"population": {$lt: 50000000}}]}, {name: 1, _id: 0})
        String queryResult = "";
        if (minPopulation != null && maxPopulation != null) {
            BasicDBList conditions = new BasicDBList();
            conditions.add(new BasicDBObject("population", new BasicDBObject("$lt", maxPopulation)));
            conditions.add(new BasicDBObject("population", new BasicDBObject("$gt", minPopulation)));
            queryResult = collection.find(new BasicDBObject("$and", conditions))
                    .projection(Projections.excludeId())
                    .map(Document::toJson)
                    .into(new ArrayList<>())
                    .toString();
        }
        else if (minPopulation != null) {
            queryResult = collection.find(new BasicDBObject("$and", new BasicDBList().add(
                            new BasicDBObject("population", new BasicDBObject("$gt", minPopulation)))))
                    .projection(Projections.excludeId())
                    .map(Document::toJson)
                    .into(new ArrayList<>())
                    .toString();;
        }
        else {
            queryResult = collection.find(new BasicDBObject("$and", new BasicDBList().add(
                            new BasicDBObject("population", new BasicDBObject("$lt", maxPopulation)))))
                    .projection(Projections.excludeId())
                    .map(Document::toJson)
                    .into(new ArrayList<>())
                    .toString();
        }
        List<Country> allCountries = simpleMapper.readValue(queryResult, new TypeReference<>(){});
        return allCountries;
    }

    public static Country retrieveCountry(String countryName) throws JsonProcessingException, NotFoundException {
        MongoCollection<Document> collection = database.getCollection("countries");
        String countryNameCapitalized = countryName.substring(0, 1).toUpperCase() +
                countryName.substring(1).toLowerCase();
        // retrieving the country from whatever language
        Document document = collection.find(new BasicDBObject("translations", countryNameCapitalized))
                .projection(Projections.excludeId()).first();
        if (document != null) {
            Country country = simpleMapper.readValue(document.toJson(), Country.class);
            return country;
        }
        throw new NotFoundException(countryName);
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

   public static void main(String[] args) {
    }
}
