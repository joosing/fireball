package practice.netty.specification;

import practice.netty.message.ProtocolMessage;
import practice.netty.processor.RxRequestProcessor;

import java.util.HashMap;
import java.util.Map;

class RxRequestProcessorManager {
    private final Map<Class<? extends ProtocolMessage>, RxRequestProcessor> classToProcessorMap;

    RxRequestProcessorManager() {
        classToProcessorMap = new HashMap<>();
    }

    RxRequestProcessor get(Class<? extends ProtocolMessage> clazz) {
        if (!classToProcessorMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in MessageSpecProvider: " + clazz);
        }
        return classToProcessorMap.get(clazz);
    }

    void put(Class<? extends ProtocolMessage> clazz, RxRequestProcessor processor) {
        classToProcessorMap.put(clazz, processor);
    }
}
