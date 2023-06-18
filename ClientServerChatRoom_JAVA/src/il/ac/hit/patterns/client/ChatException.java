package il.ac.hit.patterns.client;

/**
 * This class represents a custom exception for chat-related errors.
 * It extends the Exception class.
 */
public class ChatException extends Exception {

    /**
     * Constructs a new ChatException object with the specified error message.
     * @param message The error message.
     */
    public ChatException(String message) {
        super(message);
    }

    /**
     * Constructs a new ChatException object with the specified error message and cause.
     * @param message The error message.
     * @param cause The cause of the exception.
     */
    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }
}