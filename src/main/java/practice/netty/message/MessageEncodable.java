package practice.netty.message;

import io.netty.buffer.ByteBufAllocator;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedMessage> encode(ByteBufAllocator allocator);
}
