package practice.netty.specification;

import practice.netty.message.DecodeFunction;

import java.util.HashMap;
import java.util.Map;

class MessageDecoderManager {
    private final Map<Integer, DecodeFunction> idToDecoderMap;

    MessageDecoderManager() {
        idToDecoderMap = new HashMap<>();
    }

    DecodeFunction getDecoder(int id) {
        if (!idToDecoderMap.containsKey(id)) {
            throw new IllegalStateException("This id is not registered in MessageDecoderProvider: " + id);
        }
        return idToDecoderMap.get(id);
    }

    void putDecoder(int id, DecodeFunction decodeFunction) {
        idToDecoderMap.put(id, decodeFunction);
    }
}
