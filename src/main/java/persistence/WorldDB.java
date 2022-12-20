package persistence;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class WorldDB {
    static MongoClient db;
    static {
        db = new MongoClient("localhost", 27017);
    }

    public static void establishConnections() {
        try {
            MongoCredential credential;
            credential = MongoCredential.createCredential("wpuser", "world", "wppassword".toCharArray());
            System.out.println("Successfully Connected" + " to the database");

            MongoDatabase database = db.getDatabase("world");
            //System.out.println("Credentials are: "+ credential);
        } catch (Exception e) {System.out.println("Connection establishment failed");
            System.out.println(e);
        }
    }

    public static void createCollection(String collectionName) {
        try {
            // establishConnections() Code
            // is defined above
            establishConnections();

            // Get the database instance
            MongoDatabase database = db.getDatabase("world");

            // Create the collection
            database.createCollection(collectionName);
            System.out.println("Collection " + collectionName + " created Successfully");
        }
        catch (Exception e) {
            System.out.println("Collection creation failed");
            System.out.println(e);
        }
    }

    public static <T> void writeManyToCollection(String dbName, String collectionName, List<T> countries) {
        MongoDatabase database = db.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documents = new ArrayList<>();
        Gson gson = new Gson();
        countries.forEach(country -> {
            documents.add(Document.parse(gson.toJson(country)));
            // ALTERNATIVE USING OBJECT MAPPER
//            try {
//                documents.add(Document.parse(objectMapper.writeValueAsString(country)));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
        });
        collection.insertMany(documents);
    }

    public static void main(String[] args) {
        //createCollection("sample");
    }
}
