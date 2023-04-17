package practice.netty.message;

import io.netty.buffer.ByteBufAllocator;
import practice.netty.handler.outbound.EncodedSubMessage;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedSubMessage> encode(ByteBufAllocator allocator);
}
