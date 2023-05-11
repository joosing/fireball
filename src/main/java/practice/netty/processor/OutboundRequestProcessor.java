package practice.netty.processor;

import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserMessage;

import java.util.List;

@FunctionalInterface
public interface OutboundRequestProcessor {
    List<ProtocolMessage> process(UserMessage message);
}
