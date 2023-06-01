package io.fireball.processor;

import io.fireball.message.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

public final class EmptyBodyRetrieveProcessor implements InboundRequestProcessor {

    public static final EmptyBodyRetrieveProcessor INSTANCE = new EmptyBodyRetrieveProcessor();

    private EmptyBodyRetrieveProcessor() {}

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        return new ArrayList<>();
    }
}
