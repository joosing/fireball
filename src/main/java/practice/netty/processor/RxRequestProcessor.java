package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.util.List;


@FunctionalInterface
public interface RxRequestProcessor {
    List<ProtocolMessage> process(ProtocolMessage message);
}
