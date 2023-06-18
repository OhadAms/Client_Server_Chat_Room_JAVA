package il.ac.hit.patterns.client;

import java.awt.*;

/**
 * This class represents the Disconnected state of a SimpleClientGUI.
 * It extends the AbstractState class.
 */
public class DisconnectedStateSimpleClientGUI extends AbstractState {

    /**
     * Overrides the setConnected method from the AbstractState class.
     * Sets the GUI components to their appropriate state when the client is disconnected.
     * @param gui The SimpleClientGUI instance.
     */
    @Override
    public void setConnected(SimpleClientGUI gui) {
        // Enable the connect button
        gui.setBtConnectEnabled(true, null);

        // Disable the send button and set its color to gray
        gui.setBtSendEnabled(false, Color.GRAY);

        // Disable the user input field and set its color to gray
        gui.setTfUserInputEnabled(false, Color.GRAY);

        // Disable the disconnect button and set its color to gray
        gui.setBtDisconnectEnabled(false, Color.GRAY);

        // Enable the client name field
        gui.setTfClientNameEnabled(true, null);

        // Enable the IP address field
        gui.setTfIpEnabled(true, null);

        // Enable the port number field
        gui.setTfPortEnabled(true, null);

        // Clear the user input text field
        gui.setTfUserInputText("");
    }
}