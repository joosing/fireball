package practice.netty.message;

import io.netty.buffer.ByteBufAllocator;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedSubMessage> encode(ByteBufAllocator allocator);
}
