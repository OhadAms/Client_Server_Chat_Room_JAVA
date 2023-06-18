package il.ac.hit.patterns;

import il.ac.hit.patterns.client.ChatException;

/**
 * The StringConsumer interface represents an entity that consumes strings.
 */
public interface StringConsumer {

    /**
     * Consumes the specified text.
     *
     * @param text The text to be consumed.
     * @throws ChatException If an error occurs while consuming the text.
     */
    public void consume(String text) throws ChatException;

}