package practice.netty.specification;

import practice.netty.message.UserMessage;
import practice.netty.processor.TxRequestProcessor;

import java.util.HashMap;
import java.util.Map;

class TxRequestProcessorManager {
    private final Map<Class<? extends UserMessage>, TxRequestProcessor> classToProcessorMap;

    TxRequestProcessorManager() {
        classToProcessorMap = new HashMap<>();
    }

    TxRequestProcessor get(Class<? extends UserMessage> clazz) {
        if (!classToProcessorMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in MessageSpecProvider: " + clazz);
        }
        return classToProcessorMap.get(clazz);
    }

    void put(Class<? extends UserMessage> clazz, TxRequestProcessor processor) {
        classToProcessorMap.put(clazz, processor);
    }
}