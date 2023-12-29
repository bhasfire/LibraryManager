package ServerSide;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import commonClasses.Item;
import org.bson.Document;

import java.util.*;

public class Catalog extends Observable{
    private final MongoCollection<Document> itemsCollection;

    public Catalog(MongoDBHandler dbHandler) {
        itemsCollection = dbHandler.getCollection("Library", "Items");
    }

    public synchronized List<Document> getAllItems() {
        List<Document> items = new ArrayList<Document>();
        for (Document item : itemsCollection.find(Filters.eq("type", "book"))) {
            items.add(item);
        }
        return items;
    }

    public synchronized Document getItemById(String itemId) {
        return itemsCollection.find(Filters.and(Filters.eq("_id", itemId), Filters.eq("type", "book"))).first();
    }

    public synchronized boolean borrowItem(String itemId, String username) {
        System.out.println("In borrowItem method: itemId = " + itemId + ", username = " + username);
        Document item = getItemById(itemId);
        if (item != null && item.getInteger("availableCopies") > 0) {
            itemsCollection.updateOne(
                    Filters.eq("_id", itemId),
                    Updates.combine(
                            Updates.inc("availableCopies", -1),
                            Updates.addToSet("currentHolders", new Document("username", username)),
                            Updates.addToSet("borrowHistory", new Document("username", username).append("checkoutDate", new Date()).append("returnDate", null))
                    )
            );
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean returnItem(String itemId, String username) {
        System.out.println("In returnItem method: itemId = " + itemId + ", username = " + username);
        Document item = getItemById(itemId);
        if (item != null) {
            System.out.println("Item found: " + item);
            List<Document> currentHolders = item.getList("currentHolders", Document.class);
            for (Document holder : currentHolders) {
                System.out.println("Checking holder: " + holder.getString("username"));
                if (username.equals(holder.getString("username"))) {
                    System.out.println("Holder found, returning the item");
                    itemsCollection.updateOne(
                            Filters.eq("_id", itemId),
                            Updates.combine(
                                    Updates.inc("availableCopies", 1),
                                    Updates.pull("currentHolders", new Document("username", username)),
                                    Updates.set("borrowHistory.$[element].returnDate", new Date())
                            ),
                            new UpdateOptions().arrayFilters(Arrays.asList(Filters.eq("element.username", username)))
                    );
                    return true;
                }
            }
        } else {
            System.out.println("Item not found");
        }
        return false;
    }

    public synchronized List<Item> searchItems(String searchTerm) {
        List<Item> foundItems = new ArrayList<Item>();

        for (Document item : itemsCollection.find(Filters.and(Filters.eq("type", "book"), Filters.regex("title", searchTerm, "i")))) {
            foundItems.add(new Item(item));
        }

        return foundItems;
    }

    public synchronized boolean addNewBook(Object bookId, String title, String author, int pages, String description) {
        Document newBook = new Document()
                .append("_id", bookId)
                .append("type", "book")
                .append("title", title)
                .append("author", author)
                .append("pages", pages)
                .append("description", description)
                .append("image", "path/to/default_image.jpg")
                .append("copies", 1)
                .append("availableCopies", 1)
                .append("currentHolders", new ArrayList<Document>())
                .append("borrowHistory", new ArrayList<Document>());

        try {
            itemsCollection.insertOne(newBook);
            setChanged();
            notifyObservers("add"); // notify observers that a new book has been added
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}