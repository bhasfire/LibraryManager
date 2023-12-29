package ServerSide;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import commonClasses.SimpleHash;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDBHandler {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static MongoCollection<Document> usersCollection; // Add this line


    private static final String URI = "mongodb+srv://bhasfire:I1ElJhrVAqVpNQDr@cluster0.1qhmicp.mongodb.net/?retryWrites=true&w=majority";
    private static final String DB = "Login";
    private static final String COLLECTION = "Info";

    public MongoDBHandler() {
        ConnectionString connString = new ConnectionString(URI);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(DB);
        collection = database.getCollection(COLLECTION);
        usersCollection = database.getCollection(COLLECTION); // Add this line
        ping();
    }


    public static void connectToDatabase() {
        try {
            // Create MongoClient with connection string
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString(URI))
                    .build();
            mongoClient = MongoClients.create(settings);

            // Connect to database and get user collection
            database = mongoClient.getDatabase(DB);
            collection = database.getCollection(COLLECTION);

            ping();
        } catch (MongoException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public static void disconnectFromDatabase() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Disconnected from database.");
        }
    }

    public boolean checkLogin(String username, String password) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION);

        // create the filter to search for the username and password
        Bson filter = Filters.and(Filters.eq("username", username), Filters.eq("password", password));

        // search for a document that matches the filter
        Document document = collection.find(filter).first();

        // if no document is found, return false
        if (document == null) {
            return false;
        }
        // if a document is found, return true
        return true;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public static void ping() {
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            System.out.println("Connected successfully to server.");
        } catch (MongoException me) {
            System.err.println("An error occurred while attempting to run a command: " + me);
        }
    }

    public MongoCollection<Document> getCollection(String library, String items) {
        MongoDatabase database = mongoClient.getDatabase(library);
        MongoCollection<Document> collection = database.getCollection(items);
        return collection;
    }
    public synchronized boolean addUser(String username, String password) {
        // Check if the user already exists
        Document existingUser = usersCollection.find(Filters.eq("username", username)).first();
        if (existingUser != null) {
            // User already exists, return false
            return false;
        }

        // Hash the password
        String hashedPassword = SimpleHash.hash(password);

        // Create a new user document
        Document newUser = new Document("username", username)
                .append("password", hashedPassword);

        // Insert the new user into the collection
        usersCollection.insertOne(newUser);

        // Return true, indicating that the user has been successfully added
        return true;
    }

    public String getHashedPassword(String username) {
        Document existingUser = usersCollection.find(Filters.eq("username", username)).first();
        if (existingUser != null) {
            return existingUser.getString("password");
        }
        return null;
    }





}
