package commonClasses;

import java.io.Serializable;

public class SearchRequest implements Serializable {
    private String searchTerm;

    public SearchRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
