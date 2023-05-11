package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.util.List;


@FunctionalInterface
public interface InboundRequestProcessor {
    List<ProtocolMessage> process(ProtocolMessage message);
}
