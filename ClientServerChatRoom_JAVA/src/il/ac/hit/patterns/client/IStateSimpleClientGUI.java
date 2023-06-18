package il.ac.hit.patterns.client;

/**
 * This interface represents a state for a SimpleClientGUI.
 */
public interface IStateSimpleClientGUI {

    /**
     * Sets the GUI components to their appropriate state.
     * @param gui The SimpleClientGUI instance.
     */
    public void setConnected(SimpleClientGUI gui);

}