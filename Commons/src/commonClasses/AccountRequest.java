package commonClasses;
import java.io.Serializable;

public class AccountRequest implements java.io.Serializable {
    private String username;
    private String password;

    public AccountRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
