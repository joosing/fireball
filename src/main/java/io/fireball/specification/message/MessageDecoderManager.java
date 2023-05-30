package io.fireball.specification.message;

import io.fireball.message.DecodeFunction;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
class MessageDecoderManager {
    private final Map<Integer, DecodeFunction> idToDecoderMap;

    MessageDecoderManager() {
        idToDecoderMap = new HashMap<>();
    }

    DecodeFunction get(int id) {
        if (!idToDecoderMap.containsKey(id)) {
            throw new IllegalStateException("This id is not registered in MessageDecoderProvider: " + id);
        }
        return idToDecoderMap.get(id);
    }

    void put(int id, DecodeFunction decodeFunction) {
        idToDecoderMap.put(id, decodeFunction);
    }
}
