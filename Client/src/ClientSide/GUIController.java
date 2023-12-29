package ClientSide;

import com.google.gson.Gson;
import commonClasses.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class GUIController extends Application implements Initializable {
    private Client client;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private ActionEvent storedEvent;
    private String loggedInUsername;
    private Socket clientSocket;

    private ObjectInputStream input;
    private ObjectOutputStream output;


    @FXML
    private Button loginButton;
    @FXML
    private Button newAccount;
    @FXML
    private Button scene1Button;
    @FXML
    private Button testButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label loginFailedLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<Item> catalogListView;
    @FXML
    private Button borrowButton;
    @FXML
    private ListView<Item> checkedOutItemsListView;
    @FXML
    private Button returnButton;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private Label accountCreationStatusLabel;
    @FXML
    private CheckBox loginAsLibrarianCheckBox;
    @FXML
    private TextField bookTitleField;
    @FXML
    private TextField bookAuthorField;
    @FXML
    private TextField bookPageCountField;
    @FXML
    private TextArea bookDescriptionArea;
    @FXML
    private Button addBookButton;
    @FXML
    private Label addBookStatusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientSide/Scene1.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            GUIController controller = loader.getController();
            controller.updateBookLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeClientSocket(String serverHost, int serverPort) {
        try {
            clientSocket = new Socket(serverHost, serverPort);
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This method is to ensure that it is called after the FXMLLoader is finished initializing the controls
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String serverHost = "localhost";
        int serverPort = 5000;
        initializeClientSocket(serverHost, serverPort);


        if (borrowButton != null) {
            borrowButton.setOnAction(event -> {
                Item selectedItem = catalogListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    borrowItem(selectedItem.getId());
                    updateBookLists();
                }
            });
        }

        if (returnButton != null) {
            returnButton.setOnAction(event -> {
                Item selectedItem = checkedOutItemsListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    returnItem(selectedItem.getId());
                    updateBookLists();
                }
            });
        }
        if (searchButton != null) {
            searchButton.setOnAction(event -> {
                System.out.println("Search button clicked: " + searchTextField.getText()); // Add this print statement
                String searchTerm = searchTextField.getText();
                if (!searchTerm.isEmpty()) {
                    searchBooks(searchTerm);
                }
            });
        }
        if (searchTextField != null) {
            searchTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) { // focus lost
                    loadAllAvailableBooks();
                }
            });
        }

    }

    @FXML
    public void switchToScene2(ActionEvent event) throws IOException {      //This is the scene to create a new account
        Parent root = FXMLLoader.load(getClass().getResource("/ClientSide/Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("create new account button pressed");
    }


    @FXML
    public void switchToScene4(ActionEvent event) throws IOException {
        // This is the scene for the librarian account
        URL url = getClass().getResource("/ClientSide/Scene4.fxml");
        Parent root = FXMLLoader.load(url);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Logging into Librarian Account");
    }


    @FXML
    public void switchToScene1(ActionEvent event) throws IOException {      //This is the starting scene
        Parent root = FXMLLoader.load(getClass().getResource("/ClientSide/Scene1.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToScene3(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientSide/Scene3.fxml"));
        Parent root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        System.out.println("Logging in");
        GUIController controller = loader.getController();
        controller.setLoggedInUsername(loggedInUsername); // Pass the loggedInUsername to the new instance
        controller.updateBookLists();
    }



    @FXML
    private void updateBookLists() {
        try {
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("get_books:" + loggedInUsername);
            out.flush();
            LibraryContent libraryContent = (LibraryContent) in.readObject();

            System.out.println("Library content received: " + libraryContent);
            System.out.println("catalogListView: " + catalogListView);
            System.out.println("checkedOutItemsListView: " + checkedOutItemsListView);

            if (catalogListView != null && libraryContent.getAvailableBooks() != null) {
                ObservableList<Item> availableBooks = FXCollections.observableArrayList(libraryContent.getAvailableBooks());
                catalogListView.setItems(availableBooks);
                System.out.println("Available books: " + availableBooks);
            }

            if (checkedOutItemsListView != null && libraryContent.getCheckedOutBooks() != null) {
                ObservableList<Item> checkedOutBooks = FXCollections.observableArrayList(libraryContent.getCheckedOutBooks());
                checkedOutItemsListView.setItems(checkedOutBooks);
                System.out.println("Checked-out books: " + checkedOutBooks);
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void sendLoginInfo(String json, ActionEvent event, String username) {
        try {
            // Convert the JSON string to a LoginData object
            Gson gson = new Gson();
            LoginData loginData = gson.fromJson(json, LoginData.class);

            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to server at " + "localhost" + ":" + "5000");

            // Get input and output streams for communicating with the server
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //for testing
            System.out.println(json);

            // Send login information to server
            out.writeObject(loginData);

            // Wait for response from server
            String response = (String)in.readObject();
            System.out.println(response);

            boolean isLibrarian = loginAsLibrarianCheckBox.isSelected();
            System.out.println("isLibrarian: " + isLibrarian);

            //Switches to scene 3 if login is successful, otherwise displays Login Failed Label
            if (response.equals("success")) {
                Platform.runLater(() -> {
                    try {
                        loggedInUsername = username;
                        if (isLibrarian) {
                            switchToScene4(storedEvent);
                        } else {
                            switchToScene3(storedEvent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                loginFailedLabel.setVisible(true);
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        storedEvent = event;

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();


        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // create a LoginData object
        LoginData info = new LoginData(username, password);

        // serialize the LoginData object to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(info);

        // send the login information to the server
        sendLoginInfo(json, event, username); // pass username as an additional parameter
    }

    @FXML
    private void borrowItem(String itemId) {
        try {
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Borrowed Item Username: " + loggedInUsername); // Use loggedInUsername
            out.writeObject(new BorrowRequest(itemId, loggedInUsername)); // Use loggedInUsername
            out.flush();

            String response = (String) in.readObject();
            System.out.println(response);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnItem(String itemId) {
        try {
            printCheckedOutItemsListView();
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Returned Item Username: " + loggedInUsername);

            out.writeObject(new ReturnRequest(itemId, loggedInUsername)); // Use loggedInUsername
            out.flush();

            String response = (String) in.readObject();
            System.out.println(response);

            socket.close();

            if (response.equals("success")) {
                updateBookLists();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchBooks(String searchTerm) {
        try {
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Send search request to the server
            out.writeObject(new SearchRequest(searchTerm));

            // Receive search results from the server
            LibraryContent searchResults = (LibraryContent) in.readObject();

            System.out.println("Search results received: " + searchResults);

            // Update catalogListView with search results
            if (catalogListView != null && searchResults.getAvailableBooks() != null) {
                ObservableList<Item> foundBooks = FXCollections.observableArrayList(searchResults.getAvailableBooks());
                catalogListView.setItems(foundBooks);
                System.out.println("Found books: " + foundBooks);
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void printCheckedOutItemsListView() {
        ObservableList<Item> items = checkedOutItemsListView.getItems();
        for (Item item : items) {
            System.out.println(item.getId());
        }
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }
    @FXML
    public void handleCreateAccount() {
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();

        // Create a new account request
        AccountRequest request = new AccountRequest(username, password);

        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // Send the account creation request to the server
            oos.writeObject(request);

            // Get the response from the server
            AccountResponse response = (AccountResponse) ois.readObject();

            // Check the response and display the appropriate message
            if (response.isSuccess()) {
                // Account created successfully
                // Switch to the login scene or show a success message
            } else {
                // Failed to create the account, display the error message
                // Show a message to the user indicating the failure
            }
            if (response.isSuccess()) {
                // Account created successfully
                // Show a success message
                accountCreationStatusLabel.setText("Account Successfully Created");
                accountCreationStatusLabel.setTextFill(Color.GREEN);
                accountCreationStatusLabel.setVisible(true);
            } else {
                // Failed to create the account, display the error message
                accountCreationStatusLabel.setText("This Username Already Exists");
                accountCreationStatusLabel.setTextFill(Color.RED);
                accountCreationStatusLabel.setVisible(true);
            }

            // Close the resources
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllAvailableBooks() {
        try {
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("get_books:" + loggedInUsername);

            LibraryContent libraryContent = (LibraryContent) in.readObject();

            if (catalogListView != null && libraryContent.getAvailableBooks() != null) {
                ObservableList<Item> availableBooks = FXCollections.observableArrayList(libraryContent.getAvailableBooks());
                catalogListView.setItems(availableBooks);
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        System.out.println("handleAddBook() called");
        Object bookId = generateNewId(); // Implement a method to generate a new unique ID
        String title = bookTitleField.getText();
        String author = bookAuthorField.getText();
        int pageCount = Integer.parseInt(bookPageCountField.getText());
        String description = bookDescriptionArea.getText();

        try {
            AddBookRequest addBookRequest = new AddBookRequest(bookId, title, author, pageCount, description);
            output.writeObject(addBookRequest);
            output.flush();
            addBookStatusLabel.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Object generateNewId() {
        int randomID = new Random().nextInt(96) + 5;
        return String.valueOf(randomID);
    }



}