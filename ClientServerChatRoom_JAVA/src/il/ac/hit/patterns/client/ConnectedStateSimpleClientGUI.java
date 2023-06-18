package il.ac.hit.patterns.client;

import java.awt.*;

/**
 * This class represents the Connected state of a SimpleClientGUI.
 * It extends the AbstractState class.
 */
public class ConnectedStateSimpleClientGUI extends AbstractState {

    /**
     * Overrides the setConnected method from the AbstractState class.
     * Sets the GUI components to their appropriate state when the client is connected.
     * @param gui The SimpleClientGUI instance.
     */
    @Override
    public void setConnected(SimpleClientGUI gui) {
        // Disable the connect button and set its color to gray
        gui.setBtConnectEnabled(false, Color.GRAY);

        // Enable the send button
        gui.setBtSendEnabled(true, null);

        // Enable the user input field
        gui.setTfUserInputEnabled(true, null);

        // Enable the disconnect button
        gui.setBtDisconnectEnabled(true, null);

        // Disable the client name field and set its color to gray
        gui.setTfClientNameEnabled(false, Color.GRAY);

        // Disable the IP address field and set its color to gray
        gui.setTfIpEnabled(false, Color.GRAY);

        // Disable the port number field and set its color to gray
        gui.setTfPortEnabled(false, Color.GRAY);
    }
}