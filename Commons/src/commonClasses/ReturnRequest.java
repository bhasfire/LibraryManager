package commonClasses;

import java.io.Serializable;

public class ReturnRequest implements Serializable {
    private String itemId;
    private String username;

    public ReturnRequest(String itemId, String username) {
        this.itemId = itemId;
        this.username = username;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUsername() {
        return username;
    }
}