package practice.netty.processor;

import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserRequest;

import java.util.List;

@FunctionalInterface
public interface OutboundRequestProcessor {
    List<ProtocolMessage> process(UserRequest message);
}
