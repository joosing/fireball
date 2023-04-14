package practice.netty.specification;

import practice.netty.message.Message;
import practice.netty.processor.RequestProcessor;

@FunctionalInterface
public interface RequestProcessorProvider {
    RequestProcessor getRequestProcessor(Class<? extends Message> clazz);
}
