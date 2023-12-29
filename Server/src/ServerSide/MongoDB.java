package ServerSide;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDB {
    private static MongoClient mongo;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    private static final String URI = "mongodb+srv://bhasfire:I1ElJhrVAqVpNQDr@cluster0.1qhmicp.mongodb.net/?retryWrites=true&w=majority";
    private static final String DB = "auction";
    private static final String COLLECTION = "items";

    public static void main(String[] args) {
        mongo = MongoClients.create(URI);
        database = mongo.getDatabase(DB);
        collection = database.getCollection(COLLECTION);
        ping();
        //update();
        findAndRead();
        mongo.close();
    }
    public static void update() {
        Document doc = new Document();
        doc.put("name", "My First Program");
        doc.put("type", "Code");
        doc.put("price", 0.0);
        doc.put("winner", "");
        collection.insertOne(doc);
        doc.put("foo", "bar");
    }
    public static void findAndRead() {
        MongoCursor cursor = collection.find(Filters.eq("name", "My First Program")).cursor();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
/*
        MongoCursor cursor1 = collection.find(Filters.eq("name", "Bedford")).cursor();
        while (cursor1.hasNext()) {
            System.out.println(cursor1.next());
        }
 */
//        MongoCursor<String> cursor2 = mongo.listDatabaseNames().iterator();
//        while (cursor2.hasNext()) {
//            System.out.println(cursor2.next());
//        }

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
}

