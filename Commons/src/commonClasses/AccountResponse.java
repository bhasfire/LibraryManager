package commonClasses;

public class AccountResponse implements java.io.Serializable {
    private boolean success;
    private String message;

    public AccountResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}