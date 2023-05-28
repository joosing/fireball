package practice.netty.specification.message;

import practice.netty.message.UserRequest;
import practice.netty.processor.OutboundRequestProcessor;

import java.util.HashMap;
import java.util.Map;

class OutboundRequestProcessorManager {
    private final Map<Class<? extends UserRequest>, OutboundRequestProcessor> classToProcessorMap;

    OutboundRequestProcessorManager() {
        classToProcessorMap = new HashMap<>();
    }

    OutboundRequestProcessor get(Class<? extends UserRequest> clazz) {
        if (!classToProcessorMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in MessageSpecProvider: " + clazz);
        }
        return classToProcessorMap.get(clazz);
    }

    void put(Class<? extends UserRequest> clazz, OutboundRequestProcessor processor) {
        classToProcessorMap.put(clazz, processor);
    }
}