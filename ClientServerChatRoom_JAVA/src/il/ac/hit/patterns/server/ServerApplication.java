package il.ac.hit.patterns.server;

import il.ac.hit.patterns.ConnectionProxy;
import il.ac.hit.patterns.client.ChatException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The ServerApplication class represents the main entry point for the server application.
 * <p>
 * It initializes the server socket, message board, and manages client connections.
 */
public class ServerApplication {

    /**
     * The main method is the entry point of the server application.
     *
     * @param args The command-line arguments.
     * @throws ChatException If an exception occurs in the chat application.
     */
    public static void main(String args[]) throws ChatException {
        // Initialize the server socket
        ServerSocket server = null;
        // Create a message board
        MessageBoard mb = new MessageBoard();
        try {
        /* Create a new server socket with the specified port number and backlog size. The server socket will
         listen for incoming connections on port 1300 and allow up to 5 pending connections in the backlog.*/
            server = new ServerSocket(1300, 5);
        } catch (IOException e) {
            System.out.println("Problem creating serverSocket");
            e.printStackTrace();
            System.exit(1); // Terminate the program if server initialization fails
        }

        Socket socket = null;

        ClientDescriptor client = null;

        ConnectionProxy connection = null;

        // Continuously accept client connections and manage them
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Accept a client connection
                socket = server.accept();

                // Create a connection proxy for the client
                connection = new ConnectionProxy(socket);

                // Create a client descriptor
                client = new ClientDescriptor();

                // Add the message board as a consumer to the client descriptor
                client.addConsumer(mb);

                // Add the client descriptor as a consumer to the connection proxy
                connection.addConsumer(client);

                /* Checks if the new connection is trying to connect with a taken username, if so - we close the connection. */
                if (mb.checkClientsNames(connection.getClientName())) {
                    // Send a message to the client that the username is already taken
                    connection.consume("Client name " + connection.getClientName() + " is already in the system, try a different user name.");
                    // Close the connection
                    connection.closeConnection();

                } else {
                    /* A valid connection with a unique username. */
                    // Add the connection proxy as a consumer to the message board
                    mb.addConsumer(connection);

                    // Start the connection
                    connection.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ChatException("Client name already in system", e);
            }
        }
    }
}




