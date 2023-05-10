package practice.netty.specification;

import practice.netty.message.ProtocolMessage;
import practice.netty.processor.InboundRequestProcessor;

@FunctionalInterface
public interface InboundRequestProcessorProvider {
    InboundRequestProcessor getInboundRequestProcessor(Class<? extends ProtocolMessage> clazz);
}
