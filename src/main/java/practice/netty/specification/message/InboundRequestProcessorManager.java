package practice.netty.specification.message;

import practice.netty.message.ProtocolMessage;
import practice.netty.processor.InboundRequestProcessor;

import java.util.HashMap;
import java.util.Map;

class InboundRequestProcessorManager {
    private final Map<Class<? extends ProtocolMessage>, InboundRequestProcessor> classToProcessorMap;

    InboundRequestProcessorManager() {
        classToProcessorMap = new HashMap<>();
    }

    InboundRequestProcessor get(Class<? extends ProtocolMessage> clazz) {
        if (!classToProcessorMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in MessageSpecProvider: " + clazz);
        }
        return classToProcessorMap.get(clazz);
    }

    void put(Class<? extends ProtocolMessage> clazz, InboundRequestProcessor processor) {
        classToProcessorMap.put(clazz, processor);
    }
}
