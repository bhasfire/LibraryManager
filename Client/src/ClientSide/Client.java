package ClientSide;

import commonClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
  private String host;
  private int port;
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void connect() throws IOException {
    socket = new Socket(host, port);
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
  }

  public void disconnect() throws IOException {
    in.close();
    out.close();
    socket.close();
  }

  public void sendLoginData(LoginData loginData, Callback<String> callback) {
    try {
      out.writeObject(loginData);
      String response = (String) in.readObject();
      callback.call(response);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void getLibraryContent(String username, Callback<LibraryContent> callback) {
    try {
      out.writeObject("get_books:" + username);
      LibraryContent libraryContent = (LibraryContent) in.readObject();
      callback.call(libraryContent);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void borrowItem(String itemId, String username, Callback<String> callback) {
    try {
      out.writeObject(new BorrowRequest(itemId, username));
      String response = (String) in.readObject();
      callback.call(response);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void returnItem(String itemId, String username, Callback<String> callback) {
    try {
      out.writeObject(new ReturnRequest(itemId, username));
      String response = (String) in.readObject();
      callback.call(response);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void searchBooks(String searchTerm, Callback<LibraryContent> callback) {
    try {
      out.writeObject(new SearchRequest(searchTerm));
      LibraryContent searchResults = (LibraryContent) in.readObject();
      callback.call(searchResults);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public interface Callback<T> {
    void call(T result);
  }
}
