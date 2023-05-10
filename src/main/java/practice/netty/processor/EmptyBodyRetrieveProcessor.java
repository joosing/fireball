package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.util.List;

public final class EmptyBodyRetrieveProcessor implements InboundRequestProcessor {

    public static final EmptyBodyRetrieveProcessor INSTANCE = new EmptyBodyRetrieveProcessor();

    private EmptyBodyRetrieveProcessor() {}

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        return List.of();
    }
}