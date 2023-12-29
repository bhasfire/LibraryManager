package commonClasses;

import org.bson.Document;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    private Document document;

    public Item(Document document) {
        this.document = document;
    }

    public String getId() {
        return document.getString("_id");
    }

    public String getType() {
        return document.getString("type");
    }

    public String getTitle() {
        return document.getString("title");
    }

    public String getAuthor() {
        return document.getString("author");
    }

    public int getPages() {
        return document.getInteger("pages");
    }

    public String getDescription() {
        return document.getString("description");
    }

    public String getImage() {
        return document.getString("image");
    }

    public int getCopies() {
        return document.getInteger("copies");
    }

    public int getAvailableCopies() {
        return document.getInteger("availableCopies");
    }

    public List<Document> getCurrentHolders() {
        return document.getList("currentHolders", Document.class);
    }

    public List<Document> getBorrowHistory() {
        return document.getList("borrowHistory", Document.class);
    }
    @Override
    public String toString() {
        return getTitle() + " - " +getAuthor();
    }
}
