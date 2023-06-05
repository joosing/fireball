package io.fireball.processor;

import io.fireball.message.ProtocolMessage;
import io.fireball.message.UserRequest;

import java.util.List;

@FunctionalInterface
public interface OutboundRequestProcessor {
    List<ProtocolMessage> process(UserRequest message) throws Exception;
}
