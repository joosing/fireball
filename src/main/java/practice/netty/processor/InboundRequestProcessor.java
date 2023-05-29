package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.io.FileNotFoundException;
import java.util.List;


@FunctionalInterface
public interface InboundRequestProcessor {
    List<ProtocolMessage> process(ProtocolMessage message) throws FileNotFoundException;
}
