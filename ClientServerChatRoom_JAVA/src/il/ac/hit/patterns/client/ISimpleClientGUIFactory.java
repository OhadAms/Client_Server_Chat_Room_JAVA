package il.ac.hit.patterns.client;

/**
 * This interface represents a factory for creating SimpleClientGUI instances.
 */
public interface ISimpleClientGUIFactory {

    /**
     * Creates a new instance of SimpleClientGUI.
     * @return A new SimpleClientGUI object.
     */
    SimpleClientGUI createSimpleClientGUI();

}