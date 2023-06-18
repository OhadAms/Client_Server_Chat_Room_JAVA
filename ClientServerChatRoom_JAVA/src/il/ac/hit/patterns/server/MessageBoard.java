package il.ac.hit.patterns.server;

import il.ac.hit.patterns.ConnectionProxy;
import il.ac.hit.patterns.StringConsumer;
import il.ac.hit.patterns.StringProducer;
import il.ac.hit.patterns.client.ChatException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The MessageBoard class represents a message board that acts as a mediator between clients.
 * <p>
 * It implements the StringConsumer and StringProducer interfaces.
 */
public class MessageBoard implements StringConsumer, StringProducer {

    private final List<StringConsumer> proxies; // List of connected clients.
    List<StringConsumer> proxiesToRemove; // List of proxies to be removed (to avoid concurrent modification).

    private final Pattern pattern; // Regular expression pattern for message format.
    private final List<String> sendTo_MessageList; // List to store sender, recipient, and message.

    public MessageBoard() {
        this.proxies = new ArrayList<>();
        this.proxiesToRemove = new ArrayList<>();
        this.pattern = Pattern.compile("^(.*?)\\s##\\$\\$\\$###\\s(.*?)\\s#\\$\\$\\$#\\s(.*)$");
        this.sendTo_MessageList = new ArrayList<>();
    }


    /**
     * Consumes the received message and performs the necessary actions based on the content.
     * <p>
     * It separates the sender, recipient, and message from the received text.
     * <p>
     * It then distributes the message to the appropriate recipients.
     *
     * @param text The received message from a client.
     * @throws ChatException If an error occurs while consuming the message.
     */
    @Override
    public void consume(String text) throws ChatException {

        StringBuilder clients = new StringBuilder();  // String representation of connected clients.

        /* Separate sender, recipient, and message. () */
        command(text);

        for (StringConsumer proxy : getProxies()) {

            String clientName = ((ConnectionProxy) proxy).getClientName();

            if (text.equals(clientName + " ->" + " has left the chat room!")) {
                ((ConnectionProxy) proxy).closeConnection();
                // Add the connection to the removal list.
                getProxiesToRemove().add(proxy);
            } else {
                /* Concat the connected names into the clients list. */
                clients.append(" ").append(clientName);
            }

        }
        /* Remove all the ended connections. */
        getProxies().removeAll(getProxiesToRemove());

        /* Distribute the message to the appropriate recipients - 'All' to all the clients if we didn't find a match in the pattern for a privet message,
        else we did found a specific client name to send to, so we go to the else block and send the message only to the client sending the message and his
        recipient other. */
        if (getSendTo_MessageList().get(1).equals("All")) {
            for (StringConsumer proxy : getProxies()) {
                try {
                    proxy.consume(clients + " $$$ " + getSendTo_MessageList().get(2));
                } catch (ChatException e) {
                    throw new ChatException("Problem Sending data through writeUTF from serverSide MessageBoard.", e);
                }
            }
        } else {
            for (StringConsumer proxy : getProxies()) {
                try {
                    if (getProxies().size() == 0) {
                        clients = new StringBuilder();
                    }
                    /* found a specific client name to send to, so we go to the else block and send
                    the message only to the client sending the message and his recipient other. */

                    if (getSendTo_MessageList().get(0).equals(((ConnectionProxy) proxy).getClientName()) ||
                        getSendTo_MessageList().get(1).equals(((ConnectionProxy) proxy).getClientName())) {
                        proxy.consume(clients + " $$$ " + getSendTo_MessageList().get(2));
                    }
                } catch (ChatException e) {
                    throw new ChatException("Problem Sending data through writeUTF from serverSide MessageBoard.", e);
                }
            }
        }

        /* Clearing the List that store sender, recipient, and message (Preparing to get a new message to handle). */
        getSendTo_MessageList().clear();
    }

    /**

     * Retrieves the pattern used by the API.
     * @return The pattern object representing the current pattern.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Retrieves the list of proxies representing connected clients.
     *
     * @return The list of proxies representing connected clients.
     */
    public List<StringConsumer> getProxies() {
        return proxies;
    }

    /**
     * Retrieves the list of proxies to be removed.
     *
     * @return The list of proxies to be removed.
     */
    public List<StringConsumer> getProxiesToRemove() {
        return proxiesToRemove;
    }

    /**
     * Retrieves the list that store the sender, recipient, and message to be sent.
     *
     * @return The list that store the sender, recipient, and message to be sent.
     */
    public List<String> getSendTo_MessageList() {
        return sendTo_MessageList;
    }

    /**
     * Adds a consumer (client) to the message board.
     *
     * @param consumer The consumer to add.
     */
    @Override
    public void addConsumer(StringConsumer consumer) {
        getProxies().add(consumer);
    }

    /**
     * Removes a consumer (client) from the message board.
     *
     * @param consumer The consumer to remove.
     */
    @Override
    public void removeConsumer(StringConsumer consumer) {
        getProxies().remove(consumer);
    }

    /**
     * Separates the sender, recipient, and message from the given text based on a predefined pattern
     * and adding the separated parts into 'sentTo_MessageList' ArrayList.
     *
     * @param text The text to separate.
     */
    public void command(@NotNull String text) {
        getSendTo_MessageList().clear();
        Matcher matcher = getPattern().matcher(text);
        if (matcher.matches()) {
            // Extract the sender and message using group indices
            getSendTo_MessageList().add(matcher.group(1));
            getSendTo_MessageList().add(matcher.group(2));
            getSendTo_MessageList().add(matcher.group(3));

            System.out.println("Sender: " + getSendTo_MessageList().get(0));
            System.out.println("ToClient: " + getSendTo_MessageList().get(1));
            System.out.println("Message: " + getSendTo_MessageList().get(2));
        } else {
            /* Did not found a pattern so recipient is All clients and message is 'text'. */
            getSendTo_MessageList().add("");
            getSendTo_MessageList().add("All");
            getSendTo_MessageList().add(text);
            System.out.println("Pattern did not match the input");
        }
    }

    /**
     * Checks if a client with the given name already exists in the system.
     *
     * @param clientName The client name to check.
     * @return {@code true} if the client name already exists, {@code false} otherwise.
     */
    public boolean checkClientsNames(String clientName) {
        for (StringConsumer proxy : getProxies()) {
            if (((ConnectionProxy) proxy).getClientName().equals(clientName)) {
                return true;
            }
        }
        return false;
    }
}

