package il.ac.hit.patterns.server;

import il.ac.hit.patterns.StringConsumer;
import il.ac.hit.patterns.StringProducer;
import il.ac.hit.patterns.client.ChatException;

public class ClientDescriptor implements StringProducer, StringConsumer {

    private StringConsumer consumer;

    @Override
    public void consume(String text) throws ChatException {
        this.consumer.consume(text);
    }

    @Override
    public void addConsumer(StringConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void removeConsumer(StringConsumer consumer) {
        this.consumer = null;
    }
}
