package il.ac.hit.patterns;

/**
 * The StringProducer interface represents an entity that produces strings and can be subscribed to by StringConsumers.
 */
public interface StringProducer {

    /**
     * Adds the specified StringConsumer as a consumer for the produced strings.
     *
     * @param consumer The StringConsumer to be added.
     */
    public void addConsumer(StringConsumer consumer);

    /**
     * Removes the specified StringConsumer as a consumer for the produced strings.
     *
     * @param consumer The StringConsumer to be removed.
     */
    public void removeConsumer(StringConsumer consumer);

}