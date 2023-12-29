# Library Management System Overview

This Library Management System is a comprehensive application designed to simulate the functionalities of an online library. It enables users to log in, browse and search for library items, borrow and return them, and view their current loans. The system is equipped with a server that manages the library's catalog and user transactions, ensuring real-time updates and concurrency handling. Additionally, it offers advanced features like librarian-specific functionalities, real-time chat, item hold and renewal options, and a user-friendly interface for an engaging library experience. This project serves as a practical embodiment of key computer science concepts, focusing on user interaction and efficient data management in a library setting.

## Programmer's Guide

### Code Structure

The project is divided into client-side, server-side, and common code.

#### Server-side:
- `Server.java`: The main class responsible for starting the server and accepting client connections.
- `ClientHandler.java`: A class responsible for handling client requests on separate threads.
- `MongoDBHandler.java`: A class responsible for managing database connections and operations.
- `Catalog.java`: A class representing the library catalog.

#### Client-side:
- `GUIController.java`: Contains all the methods and FXML IDs for managing the frontend.
- `Client.java`: Establishes a socket connection with the server and sends/receives objects.

#### Common code:
- `commonCode.jar`: Shared code that exists in both the client-side and server-side, containing common classes and requests.

### Starting the Server

To start the server locally, run the `Server.java` file. The server listens on a specified port and handles incoming connections. Make sure the server-side dependencies, such as MongoDB, are installed and configured correctly.

*Developer: Boris He bdh2778*

## User's Guide

### How to Use the Client

1. Run the client application (`GUIController.java`) to launch the Library Management System GUI.
2. Enter your username and password, then click the "Login" button.
   - If you don't have an account, click "Create Account" and fill in the required fields. After successfully creating an account, return to the login screen and log in.
3. Once logged in, you will see the available books in the library catalog.

### Features

- Browse available books in the library catalog.
- Search for books by entering a search term in the search field and pressing the "Search" button.
- Borrow a book by selecting it in the catalog and clicking the "Borrow" button.
- View your checked-out books in the "Checked Out Items" tab.
- Return a checked-out book by selecting it in the "Checked Out Items" tab and clicking the "Return" button.
- Log out by clicking the "Log Out" button.

### Optional Features Implemented

- Nice GUI (1-5)
- Use of Observer class and interface (3)
- Ability to create a new account (2)
- Cloud host Server on AWS (3-5)
- Use of MongoDB (4)
- Librarian Account - ability to add books (3)
- Due dates to auto return (1)
- Password encryption (2)
- Run from Jar (1)
- Created own Commons jar?
