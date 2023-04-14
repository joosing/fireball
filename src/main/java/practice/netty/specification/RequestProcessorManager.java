package practice.netty.specification;

import practice.netty.message.Message;
import practice.netty.processor.RequestProcessor;

import java.util.HashMap;
import java.util.Map;

class RequestProcessorManager {
    private final Map<Class<? extends Message>, RequestProcessor> classToProcessorMap;

    RequestProcessorManager() {
        classToProcessorMap = new HashMap<>();
    }

    RequestProcessor getRequestProcessor(Class<? extends Message> clazz) {
        if (!classToProcessorMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in MessageSpecProvider: " + clazz);
        }
        return classToProcessorMap.get(clazz);
    }

    void putRequestProcessor(Class<? extends Message> clazz, RequestProcessor processor) {
        classToProcessorMap.put(clazz, processor);
    }
}
