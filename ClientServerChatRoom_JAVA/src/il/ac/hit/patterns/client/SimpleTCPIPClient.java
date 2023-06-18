package il.ac.hit.patterns.client;

import il.ac.hit.patterns.ConnectionProxy;
import il.ac.hit.patterns.StringConsumer;

import javax.swing.*;


/**
 * The SimpleTCPIPClient class is a client application that communicates with a server using TCP/IP.
 * It contains a main method that serves as the entry point of the program.
 */
public class SimpleTCPIPClient {

    static SimpleClientGUIFactory simpleClientGUIFactory = new SimpleClientGUIFactory();
    private static final SimpleClientGUI gui = simpleClientGUIFactory.createSimpleClientGUI();

    /**
     * Returns the GUI instance.
     *
     * @return The GUI instance.
     */
    public static SimpleClientGUI getGui() {
        return gui;
    }

    /**
     * The SimpleConsumer class implements the StringConsumer interface.
     * It consumes messages received from the server.
     */
    static class SimpleConsumer implements StringConsumer {

        /**
         * The consume method processes the received message.
         * It checks if the server approved the connection and handles different scenarios accordingly.
         * If the connection is not approved, it closes the connection and sets the GUI state to disconnected.
         * If the connection is approved, it clears the chat text field, sets the GUI state to connected,
         * and prints the new client message into the chat text area.
         * If the message is a regular text from a client to other clients, it simply prints the message in the chat text area.
         *
         * @param text The received message from the server.
         */
        @Override
        public void consume(String text) {
            System.out.println("'" + text + "' has just arrived from the server   thread=" + Thread.currentThread().getName());
            String message = ExtractMessage(text);

            /* Do if server didn't approve the connection. */
            if (text.equals("Client name " + getGui().getTfClientName().getText() + " is already in the system, try a different user name.")) {
                getGui().consumeToGuiTextArea(text);
                getGui().getProxy().closeConnection();
                getGui().setState(getGui().getDisconnectedState());
                getGui().getState().setConnected(getGui());

            /* Do if the server accepted the connection. */
            } else if (message.equals(gui.getTfClientName().getText().
                    replaceAll("\\s", "") + " ->" + getGui().getTfUserInput().getText() + " has entered the chat room!")) {
                getGui().setTaChatText("");
                getGui().setState(getGui().getConnectedState());
                getGui().getState().setConnected(getGui());
                getGui().consume(text);

            /* Do if the message received was a regular message between clients. */
            } else {
                getGui().consume(text);
            }
        }

        /**
         * Extracts the message from the received text.
         * The message is the part after the "$$$" separator.
         *
         * @param text The received text.
         * @return The extracted message.
         */
        public String ExtractMessage(String text) {
            String message = "";
            // Split the text by "$$$" and capture the first part
            String[] parts = text.split("\\$\\$\\$");
            // Extract the message
            if (parts.length > 1) {
                message = parts[1].trim();
            }
            return message;
        }
    }

    /**
     * The main method is the entry point of the program.
     * It initializes the necessary objects and starts the GUI on the event dispatch thread.
     *
     * @param args The command-line arguments.
     * @throws ChatException If an exception occurs in the chat application.
     */
    public static void main(String[] args) throws ChatException {

        System.out.println("about to create a ConnectionProxy object  thread=" + Thread.currentThread().getName());

        String serverIpAddress = "127.0.0.1";
        int serverListeningPort = 1300;

        /*  Create a new UniqueUsernameGenerator object. */
        UniqueUsernameGenerator usernameGenerator = new UniqueUsernameGenerator();

        /* Generating a new default username via the generateUsername() function of 'usernameGenerator'. */
        String userName = usernameGenerator.generateUsername();

        /* Creating a new proxy connection. */
        var proxy = new ConnectionProxy(serverIpAddress, serverListeningPort, userName);
        System.out.println("we now have a ConnectionProxy object   thread=" + Thread.currentThread().getName());

        /*
         The GUIGenerator class implements the Runnable interface.
         It is responsible for generating and initializing the GUI for the client.
         The run method is the main entry point for the GUIGenerator class.
         */

        class GUIGenerator implements Runnable {

            /**
             The run method is the main entry point for the GUIGenerator class.

             It performs the necessary steps to initialize the GUI, add consumers, and start the proxy and GUI.
             */

            @Override
            public void run() {
                try {
                    System.out.println("inside the run() of the GUIGenerator class thread=" + Thread.currentThread().getName());
                    System.out.println("The SimpleClientGUI class was instantiated thread=" + Thread.currentThread().getName());

                    SimpleConsumer simpleConsumer = new SimpleConsumer();
                    System.out.println("consumer was added to the ConnectionProxy object   thread=" + Thread.currentThread().getName());
                    proxy.addConsumer(simpleConsumer);
                    getGui().setProxy(proxy);
                    System.out.println("The ConnectionProxy object was added as a consumer to the SimpleClientGUI object thread=" + Thread.currentThread().getName());
                    getGui().addConsumer(proxy);
                    proxy.start();
                    System.out.println("The start method was called on a SimpleClientGUI object thread=" + Thread.currentThread().getName());
                    getGui().start();
                } catch (ChatException e) {
                    try {
                        throw new ChatException("Problem connecting to gui with default clientName.", e);
                    } catch (ChatException ex) {
                        System.out.println("Problem connecting to gui with default clientName.");
                    }
                }

            }
        }
        /* initializes the necessary objects and starts the GUI on the event dispatch thread. */
        SwingUtilities.invokeLater(new GUIGenerator());

    }
}