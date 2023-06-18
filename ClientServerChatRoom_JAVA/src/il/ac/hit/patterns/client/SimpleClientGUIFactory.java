package il.ac.hit.patterns.client;

/**
 * This class represents a factory for creating SimpleClientGUI instances.
 * It implements the ISimpleClientGUIFactory interface.
 */
public class SimpleClientGUIFactory implements ISimpleClientGUIFactory {

    /**
     * Creates a new instance of SimpleClientGUI.
     * @return A new SimpleClientGUI object.
     */
    public SimpleClientGUI createSimpleClientGUI() {
        return new SimpleClientGUI();
    }

}