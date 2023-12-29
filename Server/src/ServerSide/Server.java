package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  private static final int SERVER_PORT = 5000;

  public static void main(String[] args) {
    try {
      // Start server on port 5000
      ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
      System.out.println("Server started on port " + SERVER_PORT);

      // Initialize the MongoDBHandler and Catalog to be shared among the ClientHandler instances
      MongoDBHandler dbHandler = new MongoDBHandler();
      Catalog catalog = new Catalog(dbHandler);

      while (true) {
        // Wait for client
        Socket socket = serverSocket.accept();
        System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

        // Create a new ClientHandler for each connected client and start the thread
        ClientHandler clientHandler = new ClientHandler(socket, dbHandler, catalog);
        clientHandler.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
