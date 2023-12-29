package commonClasses;

import java.io.Serializable;

public class AddBookRequest implements Serializable {
    private Object bookId;
    private String title;
    private String author;
    private int pages;
    private String description;

    public AddBookRequest(Object bookId, String title, String author, int pages, String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.description = description;
    }

    public Object getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }

    public String getDescription() {
        return description;
    }
}
