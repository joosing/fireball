package practice.netty.specification;


import practice.netty.message.UserMessage;
import practice.netty.processor.OutboundRequestProcessor;

@FunctionalInterface
public interface OutboundRequestProcessorProvider {
    OutboundRequestProcessor getOutboundRequestProcessor(Class<? extends UserMessage> clazz);
}
