package practice.netty.specification.message;

import practice.netty.message.MessageEncodable;

import java.util.HashMap;
import java.util.Map;

class ProtocolIdManager {
    private final Map<Class<? extends MessageEncodable>, Integer> classToIdMap;

    ProtocolIdManager() {
        classToIdMap = new HashMap<>();
    }

    void put(Class<? extends MessageEncodable> clazz, int id) {
        classToIdMap.put(clazz, id);
    }

    int get(Class<? extends MessageEncodable> clazz) {
        if (!classToIdMap.containsKey(clazz)) {
            throw new IllegalStateException("This class is not registered in EncodingIdProvider: " + clazz);
        }
        return classToIdMap.get(clazz);
    }
}
