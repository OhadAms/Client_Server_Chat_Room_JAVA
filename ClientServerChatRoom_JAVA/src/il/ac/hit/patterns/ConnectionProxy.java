package il.ac.hit.patterns;

import il.ac.hit.patterns.client.ChatException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This class represents a connection proxy that acts as a thread and implements the StringConsumer and StringProducer interfaces.
 */
public class ConnectionProxy extends Thread implements StringConsumer, StringProducer {

    private StringConsumer consumer = null;
    private Socket socket;
    private final InputStream is;
    private final OutputStream os;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private String clientName;

    /**
     * Constructs a ConnectionProxy object for an incoming connection from a client.
     * @param socket The Socket object representing the connection.
     * @throws IOException If an I/O error occurs while creating the streams.
     */
    public ConnectionProxy(Socket socket) throws IOException {
        try {
            this.socket = socket;
            is = socket.getInputStream();
            os = socket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            setClientName(dis.readUTF()); // Here we read the client name arrived from the new connection of the client
        } catch (IOException e) {
            throw new IOException("Cant create server Socket.");
        }

    }

    /**
     * Constructs a ConnectionProxy object for an outgoing connection to a server.
     * @param computer The server's IP address or hostname.
     * @param port The server's port number.
     * @param clientName The name of the client.
     * @throws ChatException If an error occurs while establishing the connection.
     */
    public ConnectionProxy(String computer, int port, String clientName) throws ChatException {
        try {
            this.socket = new Socket();
            this.clientName = clientName;
            socket.connect(new InetSocketAddress(computer, port), 500); // If connection wasn't established in 1 second then throw exception.
            is = socket.getInputStream();
            os = socket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            dos.writeUTF(getClientName()); // Sending the name of the client to the server.
        } catch (IOException e) {
            throw new ChatException(e.getMessage(), e);
        }
    }

    /**
     * Sets the client name.
     * @param clientName The client name to be set.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Retrieves the client name.
     * @return The client name.
     */
    public String getClientName() {
        return clientName;
    }


    /**
     * {@inheritDoc}
     *
     * This method consumes the given text by writing it to the output stream of the ConnectionProxy object.
     * It also prints debug information about the process.
     *
     * @param text The text to be consumed.
     * @throws ChatException If an error occurs while writing the text to the data output stream.
     */
    @Override
    public void consume(String text) throws ChatException {
        try {
            System.out.println("inside consume invoked on a ConnectionProxy object  thread=" + Thread.currentThread().getName());
            dos.writeUTF(text);
            System.out.println("'" + text + "' was sent through dos.writeUTF  thread=" + Thread.currentThread().getName());
        } catch (IOException e) {
            throw new ChatException("Problem writing text through the data output stream", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Adds the specified StringConsumer as a consumer for the ConnectionProxy object.
     *
     * @param consumer The StringConsumer to be added.
     */
    @Override
    public void addConsumer(StringConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * {@inheritDoc}
     *
     * Removes the specified StringConsumer as a consumer from the ConnectionProxy object.
     *
     * @param consumer The StringConsumer to be removed.
     */
    @Override
    public void removeConsumer(StringConsumer consumer) {
        this.consumer = null;
    }

    /**
     * Closes the connection and releases resources.
     */
    public void closeConnection() {
        if (socket != null) {
            //System.out.println("in closeConnection");
            try {
                is.close();
                os.close();
                dis.close();
                dos.close();
                socket.close();
                socket = null;
                removeConsumer(this.consumer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Runs the thread and continuously reads incoming messages from the input stream of the ConnectionProxy object.
     * If an exception occurs, the loop is terminated.
     */
    @Override
    public void run() {
        while (true) {
            try {
                consumer.consume(dis.readUTF());
                if (socket != null) {
                    if (socket.isClosed()) {
                        System.out.println("Socket closed!");
                    }
                }
            } catch (IOException | ChatException e) {
                break;
            }
        }
    }
}