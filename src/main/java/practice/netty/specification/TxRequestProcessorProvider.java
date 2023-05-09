package practice.netty.specification;


import practice.netty.message.UserMessage;
import practice.netty.processor.TxRequestProcessor;

@FunctionalInterface
public interface TxRequestProcessorProvider {
    TxRequestProcessor getTxRequestProcessor(Class<? extends UserMessage> clazz);
}
