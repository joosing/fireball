package practice.netty.specification;

import practice.netty.message.ProtocolMessage;
import practice.netty.processor.RxRequestProcessor;

@FunctionalInterface
public interface RxRequestProcessorProvider {
    RxRequestProcessor getRxRequestProcessor(Class<? extends ProtocolMessage> clazz);
}
