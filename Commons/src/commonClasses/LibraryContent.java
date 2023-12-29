package commonClasses;

import java.io.Serializable;
import java.util.List;

public class LibraryContent implements Serializable {
    private List<Item> availableBooks;
    private List<Item> checkedOutBooks;

    public LibraryContent(List<Item> availableBooks, List<Item> checkedOutBooks) {
        this.availableBooks = availableBooks;
        this.checkedOutBooks = checkedOutBooks;
    }

    public List<Item> getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(List<Item> availableBooks) {
        this.availableBooks = availableBooks;
    }

    public List<Item> getCheckedOutBooks() {
        return checkedOutBooks;
    }

    public void setCheckedOutBooks(List<Item> checkedOutBooks) {
        this.checkedOutBooks = checkedOutBooks;
    }
}
