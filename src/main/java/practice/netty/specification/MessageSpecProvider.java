package practice.netty.specification;

import practice.netty.message.DecodeFunction;
import practice.netty.message.MessageEncodable;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageSpecProvider implements EncodingIdProvider, MessageDecoderProvider {
    protected final Map<Class<? extends MessageEncodable>, Integer> classToIdMap;
    protected final Map<Integer, DecodeFunction> idToDecoderMap;

    protected MessageSpecProvider() {
        classToIdMap = new HashMap<>();
        idToDecoderMap = new HashMap<>();
        configClassToIdMap();
        configIdToDecoderMap();
    }

    protected abstract void configClassToIdMap();
    protected abstract void configIdToDecoderMap();

    @Override
    public int getId(Class<? extends MessageEncodable> clazz) {
        throwIfNotFoundClass(clazz);
        return classToIdMap.get(clazz);
    }

    @Override
    public DecodeFunction getDecoder(int id) {
        throwIfNotFoundId(id);
        return idToDecoderMap.get(id);
    }

    private void throwIfNotFoundClass(Class<? extends MessageEncodable> clazz) {
        if (!classToIdMap.containsKey(clazz)) {
            throw new IllegalStateException("this class is not registered in MessageSpecProvider: " + clazz);
        }
    }

    private void throwIfNotFoundId(int id) {
        if (!idToDecoderMap.containsKey(id)) {
            throw new IllegalStateException("this id is not registered in MessageSpecProvider: " + id);
        }
    }
}
