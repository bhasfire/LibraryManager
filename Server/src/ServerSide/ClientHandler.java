package ServerSide;

import commonClasses.*;
import org.bson.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ClientHandler extends Thread implements Observer {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MongoDBHandler dbHandler;
    private Catalog catalog;

    public ClientHandler(Socket socket, MongoDBHandler dbHandler, Catalog catalog) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.dbHandler = dbHandler;
        this.catalog = catalog; // Fixed this line
        this.catalog.addObserver(this);
    }

    @Override
    public void run() {
        try {
            // Process client requests here
            // Call the appropriate methods on the Catalog class, and send back the response to the client
            Object obj = in.readObject();

            if (obj instanceof LoginData) {
                handleLogin((LoginData) obj);
            }
            if (obj instanceof SearchRequest) {
                handleSearchRequest((SearchRequest) obj);
            }
            if (obj instanceof AddBookRequest) {
                handleAddBookRequest((AddBookRequest) obj);
            }
            else if (obj instanceof BorrowRequest) {
                BorrowRequest request = (BorrowRequest) obj;
                handleBorrowItem(request.getItemId(), request.getUsername());
            } else if (obj instanceof ReturnRequest) {
                ReturnRequest request = (ReturnRequest) obj;
                handleReturnItem(request.getItemId(), request.getUsername());
            }
            // Handle AccountRequest
            else if (obj instanceof AccountRequest) {
                AccountRequest request = (AccountRequest) obj;
                handleCreateAccount(request);
            }
            //...
            else if (obj instanceof String) {
                String request = (String) obj;
                if (request.startsWith("get_books:")) {
                    String username = request.split(":")[1];
                    handleGetBooks(username);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String action = (String) arg;
            System.out.println("Received action: " + action);
        }
    }


    private void handleBorrowItem(String itemId, String username) throws IOException {
        System.out.println("In handleBorrowItem: itemId = " + itemId + ", username = " + username);
        boolean success = catalog.borrowItem(itemId, username);
        out.writeObject(success ? "success" : "failed");
        out.flush();
    }

    private void handleReturnItem(String itemId, String username) throws IOException {
        System.out.println("In handleReturnItem: itemId = " + itemId + ", username = " + username);
        boolean success = catalog.returnItem(itemId, username);
        out.writeObject(success ? "success" : "failed");
        out.flush();
    }


    private void handleGetBooks(String username) throws IOException {
        List<Document> documents = catalog.getAllItems();
        List<Item> allItems = new ArrayList<Item>();
        List<Item> availableItems = new ArrayList<Item>();
        List<Item> checkedOutItems = new ArrayList<Item>();

        for (Document document : documents) {
            Item item = new Item(document);
            allItems.add(item);

            if (item.getAvailableCopies() > 0) {
                availableItems.add(item);
            }

            List<Document> currentHolders = document.getList("currentHolders", Document.class);
            for (Document holder : currentHolders) {
                if (username.equals(holder.getString("username"))) {
                    checkedOutItems.add(item);
                    break;
                }
            }
        }

        if (availableItems == null) {
            availableItems = new ArrayList<Item>();
        }

        if (checkedOutItems == null) {
            checkedOutItems = new ArrayList<Item>();
        }

        LibraryContent libraryContent = new LibraryContent(availableItems, checkedOutItems);
        out.writeObject(libraryContent);
        out.flush();
    }



    private void handleLogin(LoginData loginData) throws Exception {
        String username = loginData.getUsername();
        String plainPassword = loginData.getPassword();
        String storedHash = dbHandler.getHashedPassword(username);

        System.out.println("Received username: " + username);
        System.out.println("Received password: " + plainPassword);
        System.out.println("Stored hash: " + storedHash);

        String computedHash = SimpleHash.hash(plainPassword);
        boolean success = storedHash.equals(computedHash);

        String response = success ? "success" : "failed";
        out.writeObject(response);
        out.flush();
    }


    // In ClientHandler.java
    private void handleSearchRequest(SearchRequest request) throws IOException {
        String searchTerm = request.getSearchTerm();
        System.out.println("In handleSearchRequest: searchTerm = " + searchTerm);

        List<Item> foundItems = catalog.searchItems(searchTerm);
        LibraryContent searchResults = new LibraryContent(foundItems, null);

        out.writeObject(searchResults);
        out.flush();
    }

    private void handleCreateAccount(AccountRequest request) throws IOException {
        String username = request.getUsername();
        String password = request.getPassword();

        boolean success = dbHandler.addUser(username, password);
        String message = success ? "Account created successfully" : "Failed to create account";

        AccountResponse response = new AccountResponse(success, message);
        out.writeObject(response);
        out.flush();
    }
    private void handleAddBookRequest(AddBookRequest request) throws IOException {
        System.out.println("handleAddBookRequest() called");
        Object bookId = request.getBookId();
        String title = request.getTitle();
        String author = request.getAuthor();
        int pages = request.getPages();
        String description = request.getDescription();

        boolean success = catalog.addNewBook(bookId, title, author, pages, description);

        System.out.println("addNewBook() returned " + success);

        out.writeObject(success ? "success" : "failed");
        out.flush();
    }



}