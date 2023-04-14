package practice.netty.specification;

import practice.netty.message.MessageEncodable;

import java.util.HashMap;
import java.util.Map;

class EncodingIdManager {
    private final Map<Class<? extends MessageEncodable>, Integer> classToIdMap;

    EncodingIdManager() {
        classToIdMap = new HashMap<>();
    }

    void putId(Class<? extends MessageEncodable> clazz, int id) {
        classToIdMap.put(clazz, id);
    }

    int getId(Class<? extends MessageEncodable> clazz) {
        if (!classToIdMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in EncodingIdProvider: " + clazz);
        }
        return classToIdMap.get(clazz);
    }
}
