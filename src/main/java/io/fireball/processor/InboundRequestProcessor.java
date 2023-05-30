package io.fireball.processor;

import io.fireball.message.ProtocolMessage;

import java.io.FileNotFoundException;
import java.util.List;


@FunctionalInterface
public interface InboundRequestProcessor {
    List<ProtocolMessage> process(ProtocolMessage message) throws FileNotFoundException;
}
