package il.ac.hit.patterns.client;

import il.ac.hit.patterns.ConnectionProxy;
import il.ac.hit.patterns.StringConsumer;
import il.ac.hit.patterns.StringProducer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleClientGUI implements StringConsumer, StringProducer {

    private static ConnectionProxy proxy; // Represents the connection proxy object.
    private StringConsumer consumer; // Represents the consumer object for handling string inputs.
    private final JFrame frame; // Represents the main frame of the GUI.
    private final JTextField tfUserInput; // Represents the text field for user input.
    private final JTextField tfIp; // Represents the text fields for IP address input.
    private final JTextField tfPort; // Represents the text fields for Port number input.
    private static JTextField tfClientName; // Represents the text field for the client name input.
    private final JTextArea taIp; // Represents the text area for displaying the label "IP Address:".
    private final JTextArea taPort; // Represents the text area for displaying the label "Port:".
    private final JTextArea taClientName; // Represents the text area for displaying the label "Enter Client Name:".
    private static JTextArea taChat; // Represents the text area for displaying the chat messages.
    private final JButton btSend; // Represents the button for sending messages.
    private final JPanel panelSouth; // Represents the panel in the south sections of the GUI.
    private final JPanel panelNorth; // Represents the panel in the north sections of the GUI.
    private final JScrollPane centerScrollPane; // Represents the scroll pane for the chat text area.
    private final JScrollPane eastScrollPane; // Represents the scroll pane for the connected user list.
    private final JButton btConnect; // Represents the button for connecting.
    private final JButton btDisconnect; // Represents the button for disconnecting.
    private static String selectedClient; // Represents the currently selected client from the connected user list.
    private Boolean newServerConnection; // Indicates if a new connection to a different server was established.
    private IStateSimpleClientGUI state; // Represents the state of the GUI (e.g., connected or disconnected).
    private final IStateSimpleClientGUI disconnectedState; // Represent the disconnectedState of the gui.
    private final IStateSimpleClientGUI connectedState; // Represent the connectedState of the gui.
    private static List<String> allClientNames; // Represents the list of all connected client names.
    private static JList<String> usernameConnectedList; // Represents the list component for displaying connected usernames.
    private static DefaultListModel<String> listModel; // Represents the model for the connected user list.



    /**
     * Constructs a SimpleClientGUI object.
     * <p>
     * This constructor initializes the graphical user interface (GUI) for the client application.
     * <p>
     * It creates a JFrame to hold all the components and sets the server connection to be true.
     * <p>
     * The GUI includes text fields for user input, text areas for displaying messages and information,
     * <p>
     * buttons for sending messages and managing the connection, and panels for organizing the layout.
     * <p>
     * The client usernames are stored in an ArrayList for tracking the connected clients.
     * <p>
     * The GUI components created in this constructor include:
     * <p>
     * JTextField: tfUserInput, tfIp, tfPort, tfClientName
     * JTextArea: taIp, taPort, taClientName, taChat
     * JButton: btSend, btConnect, btDisconnect
     * JPanel: panelSouth, panelNorth
     * JScrollPane: centerScrollPane (for taChat), eastScrollPane (for usernameConnectedList)
     * The GUI elements are configured with appropriate settings, such as setting the taChat to be uneditable,
     * <p>
     * setting the selection mode for usernameConnectedList, and creating the listModel for usernameConnectedList.
     * <p>
     * Example usage:
     * <p>
     * SimpleClientGUI clientGUI = new SimpleClientGUI();
     *
     * @see JFrame
     * @see JTextField
     * @see JTextArea
     * @see JButton
     * @see JPanel
     * @see JScrollPane
     */
    public SimpleClientGUI() {

        /* Creating a new instance of the DisconnectedStateSimpleClientGUI and ConnectedStateSimpleClientGUI object. */
        disconnectedState = new DisconnectedStateSimpleClientGUI();
        connectedState = new ConnectedStateSimpleClientGUI();

        /* Setting the server connection to be true */
        setNewServerConnection(true);

        /* Creating a new JFrame named 'frame'*/
        frame = new JFrame();


        // Creating a new textField for 'tfUserInput', 'tfIp', 'tfPort', 'tfClientName' user input fields.
        tfUserInput = new JTextField(10);
        tfIp = new JTextField(10);
        tfPort = new JTextField(10); // Creating a new textField for the 'Port' user input.
        tfClientName = new JTextField(10);


        /* Creating a new JTextarea for 'taIp' with the text 'IP Address:' and for 'taPort' with the text 'Port:'
         * and for 'taClientName' with the text 'Enter Client Name:' */
        taIp = new JTextArea("IP Address:");
        taPort = new JTextArea("Port:");
        taClientName = new JTextArea("Enter Client Name:");


        /* Creating a new JTextarea for 'taChat' that will store all the messages from the client. */
        taChat = new JTextArea();


        /* We are setting the 'chat', 'toPort', 'taIp', 'taClientName' textAreas to be un-editable. */
        getTaChat().setEditable(false);
        getTaPort().setEditable(false);
        getTaIp().setEditable(false);
        getTaClientName().setEditable(false);

        /* Creating GUI buttons and panels */
        btSend = new JButton("Send");
        btConnect = new JButton("Connect");
        btDisconnect = new JButton("Disconnect");
        panelSouth = new JPanel();
        panelNorth = new JPanel();
        centerScrollPane = new JScrollPane(getTaChat()); // Adding a scroll option to the 'taChat'


        /* Creating and adding all the components for the 'usernameConnectedList'. */
        listModel = new DefaultListModel<>();
        usernameConnectedList = new JList<>(getListModel()); // Connecting the listModel to the 'usernameConnectedList'.
        getUsernameConnectedList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allowing only one list item to be selected at a time.
        eastScrollPane = new JScrollPane(getUsernameConnectedList()); // Adding a scroll option to the 'eastScrollPane' that holds the 'usernameConnectedList'.


        /* Creating the ArrayList that will hold all the connected usernames of the clients. */
        allClientNames = new ArrayList<>();
    }

    /**
     * Starts the chat client GUI and initializes the UI components.
     * Sets up the layout, colors, and default values for the GUI elements.
     * Adds action listeners to the buttons and mouse listener to the username list.
     * Sends a message to the server indicating that the client has entered the chat room.
     *
     * @throws ChatException if there is an error connecting to the server.
     */
    public void start() throws ChatException {

        /* Setting the layout for the JFrame 'frame' and the JPanel 'panelSouth' */
        getFrame().setLayout(new BorderLayout());
        getPanelSouth().setLayout(new FlowLayout());

        /* Setting the color of the North and South panel. */
        getPanelNorth().setBackground(Color.ORANGE.brighter());
        getPanelSouth().setBackground(Color.ORANGE.brighter());

        /* Adding all the elements for the north JFrame panel on the north panel */
        getPanelNorth().add(getTaClientName());
        getPanelNorth().add(getTfClientName());
        getPanelNorth().add(getTaIp());
        getPanelNorth().add(getTfIp());
        getPanelNorth().add(getTaPort());
        getPanelNorth().add(getTfPort());
        getPanelNorth().add(getBtConnect());
        getPanelNorth().add(getBtDisconnect());


        /* Setting the default values for the 'tfClientName', 'tfIp', 'tfPort' jTextFields. */
        getTfClientName().setText(getProxy().getClientName());
        getTfIp().setText("127.0.0.1");
        getTfPort().setText("1300");


        /* Adding all the elements for the south JFrame panel on the south panel */
        getPanelSouth().add(getTfUserInput());
        getPanelSouth().add(getBtSend());


        /* Getting the preferred size for our JList 'usernameConnectedList' */
        Dimension usernamesListDimension = getUsernameConnectedList().getPreferredSize();
        /* Setting the width of the JList 'usernameConnectedList' that holds all the connected usernames */
        usernamesListDimension.width = 150;
        /* Setting the preferred size of the 'eastScrollPane' */
        getEastScrollPane().setPreferredSize(usernamesListDimension);


        /* Adding all the panels to the main frame */
        getFrame().add(getPanelNorth(), BorderLayout.NORTH);
        getFrame().add(getPanelSouth(), BorderLayout.SOUTH);
        getFrame().add(getCenterScrollPane(), BorderLayout.CENTER);
        getFrame().add(getEastScrollPane(), BorderLayout.EAST);


        getFrame().setSize(800, 500); // Setting the size of our main frame.
        getFrame().setVisible(true); // Setting the visibility of the frame to be true.


        /* Adding action listeners for buttons 'btSend', 'btConnect', 'btDisconnect' and 'usernameConnectedList' */
        getBtSend().addActionListener(new ButtonsObserver());
        getBtConnect().addActionListener(new ButtonsObserver());
        getBtDisconnect().addActionListener(new ButtonsObserver());
        getUsernameConnectedList().addMouseListener(new UsernameConnectedListMouseListener());

        try {
            // Sending a message to the server that the client has entered the chat room (for the first user a new GUI instance).
            getConsumer().consume(getTfClientName().getText().replaceAll("\\s", "") + " ->" + getTfUserInput().getText() + " has entered the chat room!");
        } catch (RuntimeException | ChatException e) {
            throw new ChatException("Could not Connect to the server, please try again.", e);
        }
    }

    /**
     * Returns the JFrame instance.
     *
     * @return The JFrame instance.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns the JPanel instance for the south panel.
     *
     * @return The JPanel instance for the south panel.
     */
    public JPanel getPanelSouth() {
        return panelSouth;
    }

    /**
     * Returns the JPanel instance for the north panel.
     *
     * @return The JPanel instance for the north panel.
     */
    public JPanel getPanelNorth() {
        return panelNorth;
    }

    /**
     * Returns the JScrollPane instance for the center area.
     *
     * @return The JScrollPane instance for the center area.
     */
    public JScrollPane getCenterScrollPane() {
        return centerScrollPane;
    }

    /**
     * Returns the JScrollPane instance for the east area.
     *
     * @return The JScrollPane instance for the east area.
     */
    public JScrollPane getEastScrollPane() {
        return eastScrollPane;
    }

    /**
     * Returns the JTextArea instance for the port field.
     *
     * @return The JTextArea instance for the port field.
     */
    public JTextArea getTaPort() {
        return taPort;
    }

    /**
     * Returns the JTextArea instance for the taIp field.
     *
     * @return The JTextArea instance for the taIp field.
     */
    public JTextArea getTaIp() {
        return taIp;
    }

    /**
     * Returns the JTextArea instance for the client name field.
     *
     * @return The JTextArea instance for the client name field.
     */
    public JTextArea getTaClientName() {
        return taClientName;
    }

    /**
     * Returns the JTextField instance for the 'tfClientName' text field.
     *
     * @return The JTextField instance for the 'tfClientName' button.
     */
    public JTextField getTfClientName() {
        return tfClientName;
    }

    /**
     * Returns the JTextField instance for the user input field.
     *
     * @return The JTextField instance for the user input field.
     */
    public JTextField getTfUserInput() {
        return tfUserInput;
    }

    /**
     * Sets the text of the user input field.
     *
     * @param text The text to be set in the user input field.
     */
    public void setTfUserInputText(String text) {
        getTfUserInput().setText(text);
    }

    /**
     * Returns the JTextField instance for the IP address field.
     *
     * @return The JTextField instance for the IP address field.
     */
    public JTextField getTfIp() {
        return tfIp;
    }

    /**
     * Returns the JTextField instance for the port field.
     *
     * @return The JTextField instance for the port field.
     */
    public JTextField getTfPort() {
        return tfPort;
    }

    /**
     * Returns the JButton instance for the 'Send' button.
     *
     * @return The JButton instance for the 'Send' button.
     */
    public JButton getBtSend() {
        return btSend;
    }

    /**
     * Returns the JButton instance for the btConnect field.
     *
     * @return The JButton instance for the btConnect field.
     */
    public JButton getBtConnect() {
        return btConnect;
    }

    /**
     * Returns the JButton instance for the 'Disconnect' button.
     *
     * @return The JButton instance for the 'Disconnect' button.
     */
    public JButton getBtDisconnect() {
        return btDisconnect;
    }

    /**
     * Returns the JTextArea instance for the chat area.
     *
     * @return The JTextArea instance for the chat area.
     */
    public JTextArea getTaChat() {
        return taChat;
    }

    /**
     * Sets the text for the chat area.
     *
     * @param text The text to be set.
     */
    public void setTaChatText(String text) {
        getTaChat().setText(text);
    }

    /**
     * Returns the JList instance for the connected usernames.
     *
     * @return The JList instance for the connected usernames.
     */
    public static JList<String> getUsernameConnectedList() {
        return usernameConnectedList;
    }

    /**
     * Returns the DefaultListModel instance for the list.
     *
     * @return The DefaultListModel instance for the list.
     */
    public static DefaultListModel<String> getListModel() {
        return listModel;
    }

    /**
     * Returns the status of the new server connection.
     *
     * @return The status of the new server connection.
     */
    public Boolean getNewServerConnection() {
        return newServerConnection;
    }

    /**
     * Sets the status of the new server connection.
     *
     * @param newServerConnection The new server connection status.
     */
    public void setNewServerConnection(Boolean newServerConnection) {
        this.newServerConnection = newServerConnection;
    }

    /**
     * Returns the list of all client names.
     *
     * @return The list of all client names.
     */
    public static List<String> getAllClientNames() {
        return allClientNames;
    }

    /**
     * Adds items to the list model and performs additional operations.
     *
     * @param items The list of items to add.
     */
    public void addItemsToList(List<String> items) {
        getListModel().addElement("All"); // Adding to the first index of the list model an element called 'All'
        for (String clientName : items) {
            getListModel().addElement(clientName); // Adding all the client names to the listModel.
        }
        getUsernameConnectedList().setSelectionInterval(0, 0); // Selecting the first element of the 'usernameConnectedList' -> 'All'.
        setSelectedClient(getUsernameConnectedList().getSelectedValue()); // Setting the 'selectedClient' to be 'All'
        System.out.println("client selected is: " + getSelectedClient());
    }

    /**
     * Removes all item from the list model.
     */
    public static void removeAllItemFromList() {
        getListModel().clear();
    }

    /**
     * Removes an item from the list model.
     *
     * @param item The item to remove.
     */
    public static void removeItemFromList(String item) {
        getListModel().removeElement(item);
    }

    /**
     * Modifies an item in the list model.
     *
     * @param oldItem The item to be modified.
     * @param newItem The new value to replace the old item.
     */

    public void modifyItemInList(String oldItem, String newItem) {
        int index = getListModel().indexOf(oldItem);
        if (index != -1) {
            getListModel().set(index, newItem);
        }
    }

    /**
     * Retrieves the currently selected client of the JList.
     *
     * @return The selected client name.
     */
    public static String getSelectedClient() {
        return selectedClient;
    }

    /**
     * Sets the selected client.
     *
     * @param selectedClient The client to be set as selected.
     */
    public static void setSelectedClient(String selectedClient) {
        SimpleClientGUI.selectedClient = selectedClient;
    }

    /**
     *
     Retrieves the state representing the disconnected state of the SimpleClientGUI.
     @return The disconnected state of the SimpleClientGUI.
     */
    public IStateSimpleClientGUI getDisconnectedState() {
        return disconnectedState;
    }
    /**

     Retrieves the state representing the connected state of the SimpleClientGUI.
     @return The connected state of the SimpleClientGUI.
     */
    public IStateSimpleClientGUI getConnectedState() {
        return connectedState;
    }

    /**
     * Responsible for returning the IStateSimpleClientGUI object named
     * 'state' of our SimpleClientGUI object.
     *
     * @return the state of the GUI.
     */
    public IStateSimpleClientGUI getState() {
        return state;
    }

    /**
     * Sets the state of the SimpleClientGUI.
     *
     * @param state The state to be set to.
     */
    public void setState(IStateSimpleClientGUI state) {
        this.state = state;
    }

    /**
     * Sets enable state and foreground color of the 'btConnect' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setBtConnectEnabled(Boolean bool, Color foregroundColor) {
        getBtConnect().setEnabled(bool);
        getBtConnect().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'btSend' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setBtSendEnabled(Boolean bool, Color foregroundColor) {
        getBtSend().setEnabled(bool);
        getBtSend().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'tfUserInput' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setTfUserInputEnabled(Boolean bool, Color foregroundColor) {
        getTfUserInput().setEnabled(bool);
        getTfUserInput().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'btDisconnect' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setBtDisconnectEnabled(Boolean bool, Color foregroundColor) {
        getBtDisconnect().setEnabled(bool);
        getBtDisconnect().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'tfClientName' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setTfClientNameEnabled(Boolean bool, Color foregroundColor) {
        getTfClientName().setEnabled(bool);
        getTfClientName().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'tfIp' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setTfIpEnabled(Boolean bool, Color foregroundColor) {
        getTfIp().setEnabled(bool);
        getTfIp().setForeground(foregroundColor);
    }

    /**
     * Sets enable state and foreground color of the 'tfPort' button.
     *
     * @param bool            The boolean value indicating enable state of the button.
     * @param foregroundColor The foreground color to be set for the button.
     */
    public void setTfPortEnabled(Boolean bool, Color foregroundColor) {
        getTfPort().setEnabled(bool);
        getTfPort().setForeground(foregroundColor);
    }

    /**
     * Consumes the provided text and updates the GUI text area, this is used to print utility messages to the client.
     *
     * @param text The text to consume.
     */
    public void consumeToGuiTextArea(@NotNull String text) {
        getTaChat().append(text + "\n");
    }

    /**
     * Returns the StringConsumer object associated with this GUI.
     *
     * @return The StringConsumer object.
     */
    public StringConsumer getConsumer() {
        return consumer;
    }

    /**

     Consumes the incoming text message received from the server.

     @param text The text message received from the server.
     */
    @Override
    public void consume(String text) {
        int selectedClient = getUsernameConnectedList().getSelectedIndex();

        String message = "";

        getAllClientNames().clear();

        // Split the text by "$$$" and capture the first part
        String[] parts = text.split("\\$\\$\\$");
        String namesText = parts[0].trim();

        // Extract the names
        if (!namesText.isEmpty()) {
            String[] nameArr = namesText.split(" ");
            getAllClientNames().addAll(Arrays.asList(nameArr));
        }

        // Extract the message
        if (parts.length > 1) {
            message = parts[1].trim();
        }

        // Append the message to the text area
        getTaChat().append(message + "\n");

        // Print the names to the console
        System.out.println("Names arrived from the server to the gui:");
        for (String name : getAllClientNames()) {
            System.out.println(name);
        }

        // Refreshing the connected client names JList.
        removeAllItemFromList();
        addItemsToList(getAllClientNames()); //TODO IF THE MODELLIST CONTAINS THE NAME LEAVE IT ELSE ADD IT TO THE MODELLIST
        if(selectedClient != -1) {
            /* Selecting the last element of the 'usernameConnectedList' that was selected. */
            getUsernameConnectedList().setSelectionInterval(selectedClient, selectedClient);
            /* Setting the 'selectedClient' to be the last selected client. */
            setSelectedClient(getUsernameConnectedList().getSelectedValue());
        }
    }

    /**
     * Overrides the addConsumer method to set the provided StringConsumer as the consumer.
     *
     * @param consumer The StringConsumer to be set.
     */
    @Override
    public void addConsumer(StringConsumer consumer) {
        this.consumer = consumer;

    }

    /**
     * Overrides the removeConsumer method to remove the current StringConsumer.
     *
     * @param consumer The StringConsumer to be removed.
     */
    @Override
    public void removeConsumer(StringConsumer consumer) {
        if (getConsumer() != null) {
            addConsumer(null);
        }
    }

    /**
     * Returns the ConnectionProxy object associated with this GUI.
     *
     * @return The ConnectionProxy object.
     */
    public ConnectionProxy getProxy() {
        return proxy;
    }

    /**
     * Sets the ConnectionProxy object for this GUI.
     *
     * @param proxy The ConnectionProxy object to set.
     */
    public void setProxy(ConnectionProxy proxy) {
        SimpleClientGUI.proxy = proxy;
    }

    /**
     * Changes the current consumer to a new ConnectionProxy with the provided server IP address, server listening port,
     * and client name. Starts the proxy, adds it as a consumer and sets the proxy of the gui to be the new proxy that was created.
     *
     * @param serverIpAddress     The IP address of the server.
     * @param serverListeningPort The listening port of the server.
     * @throws ChatException If there is a problem with the chat functionality.
     * @throws IOException   If there is an IO exception.
     */
    public void changeConsumer(String serverIpAddress, String serverListeningPort) throws ChatException, IOException {
        System.out.println("inside changeConsumer");
        try {

            var proxy = new ConnectionProxy(serverIpAddress, Integer.parseInt(serverListeningPort), getTfClientName().getText());
            SimpleTCPIPClient.SimpleConsumer simpleConsumer = new SimpleTCPIPClient.SimpleConsumer();
            proxy.addConsumer(simpleConsumer);
            proxy.start();
            addConsumer(proxy);
            setProxy(proxy);
            setNewServerConnection(true); // the connection to the server was successful.

        } catch (Exception e) {
            System.out.println("Problem with port/ip address");
            /* If we couldn't establish a connection than show this message to the client. */
            consumeToGuiTextArea("Problem Connecting to the server, recheck the entered ip and port number");
            setNewServerConnection(false); // could not connect to server.
        }
    }

    /**
     * The ButtonsObserver class is implementing the ActionListener interface,
     * which means it is serving as an observer for button events.
     * It is responsible for handling the actions performed when buttons are clicked.
     */
    class ButtonsObserver implements ActionListener {

        /**
         * Performs the appropriate actions based on the button that was clicked.
         *
         * @param actionEvent The ActionEvent object representing the button click event.
         * @throws RuntimeException If an I/O error occurs or a ChatException is thrown while attempting to establish a connection.
         */
        @Override
        public void actionPerformed(@NotNull ActionEvent actionEvent) throws RuntimeException {
            try {
                /* If btConnect was pressed do the following */

                if (actionEvent.getSource() == getBtConnect()) {
                    System.out.println("Connect was pressed");
                    try {
                        /* Must have a name to try and connect to the server. */

                        if (!getTfClientName().getText().equals("")) {

                            /* Try to establish a new connection. */
                            changeConsumer(getTfIp().getText(), getTfPort().getText());

                            /* If a new connection has been established then we are alerting all clients that
                            the new client has entered the room (Sending the message to the server and the server to every client). */
                            if (getNewServerConnection()) {
                                getConsumer().consume(getTfClientName().getText().
                                        replaceAll("\\s", "") + " ->" + getTfUserInput().getText() + " has entered the chat room!");
                                System.out.println("in get new connection true");
                            }
                        } else {
                            consumeToGuiTextArea("System message -> Must enter a Username.");
                        }
                    } catch (IOException | ChatException e) {
                        throw new ChatException("Problem completing code:", e);
                    }
                }
                /* If btDisconnect was pressed do the following */

                else if (actionEvent.getSource() == getBtDisconnect()) {
                    System.out.println("btDisconnect was pressed");

                    /* Setting the state to be of a 'DisconnectedStateSimpleClientGUI' object. */
                    setState(getDisconnectedState());
                    /* Getting the state object assigned to the GUI and making the changes required via the 'setConnected(gui)' function of it. */
                    getState().setConnected(SimpleClientGUI.this);

                    /* If a client was disconnected from the server then we are alerting all clients that
                       the client has left the room (Sending the message to the server and the server to every client). */

                    getConsumer().consume(getTfClientName().getText().
                            replaceAll("\\s", "") + " ->" + " has left the chat room!");

                    removeAllItemFromList(); // removes the client from the list of connected users after he disconnects.

                    setTaChatText("");
                    getProxy().closeConnection();
                }
                /* If btSend was pressed do the following */

                else if (actionEvent.getSource() == getBtSend()) {
                    if(!getTfUserInput().getText().equals("")) {
                        System.out.println("inside actionPerformed... thread=" + Thread.currentThread().getName() + " tf.getText()=" + getTfUserInput().getText());

                        /* If a user is trying to attack the system using the special pattern we use to split the message
                        to different meanings, this line of code will prevent him from succeeding. */
                        if (getTfUserInput().getText().contains("##$$$###") || getTfUserInput().getText().contains("#$$$#")) {
                            consumeToGuiTextArea("Message Pattern not allowed - try sending a different message.");
                            return;
                        }

                        /* Sending to the server the client name with no spaces along with, client name to send the message to, and his message. */
                        getConsumer().consume(getProxy().getClientName() + " ##$$$### " + getSelectedClient() + " #$$$# " + getTfClientName().getText().
                                replaceAll("\\s", "") + " -> " + getTfUserInput().getText());

                        System.out.println(getProxy().getClientName() + " ##$$$### " + getSelectedClient() + " #$$$# " + getTfClientName().getText().
                                replaceAll("\\s", "") + " -> " + getTfUserInput().getText() +
                                " was sent to the server by calling the consumer.consume() method thread=" + Thread.currentThread().getName());
                        setTfUserInputText(""); // Clearing the tfUserInput field after every message he sends.
                    }
                    else {
                        System.out.println("Client tried to send an empty message.");
                    }
                }
            } catch (ChatException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * MouseAdapter implementation that listens for mouse clicks on the username connected list in the GUI.
     * This class handles the selection of a username from the list to send a message to.
     */
    public static class UsernameConnectedListMouseListener extends MouseAdapter {

        /**
         * Invoked when a mouse click event occurs on the username connected list.
         *
         * @param e The MouseEvent object representing the mouse click event.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int index = getUsernameConnectedList().locationToIndex(e.getPoint());
                toggleItem(index);
                System.out.println("index is:" + index);
            }
        }

        /**
         * Toggles the selection of an item at the specified index in the username connected list.
         *
         * @param index The index of the item to toggle.
         */
        private void toggleItem(int index) {
            if (index >= 0 && index < getListModel().size()) {
                setSelectedClient(getListModel().getElementAt(index));
                getListModel().setElementAt(getSelectedClient(), index);
                System.out.println("Element that was chosen is: " + getSelectedClient() + " at index: " + index);
            }
        }
    }
}
