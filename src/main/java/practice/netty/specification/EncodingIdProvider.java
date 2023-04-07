package practice.netty.specification;

import practice.netty.message.MessageEncodable;

@FunctionalInterface
public interface EncodingIdProvider {
    int getId(Class<? extends MessageEncodable> clazz);
}
