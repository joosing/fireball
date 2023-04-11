package practice.netty.handler.inbound;

import io.netty.buffer.ByteBufAllocator;
import practice.netty.message.Message;

import java.util.Optional;

@FunctionalInterface
public interface ResponseFactory {
    Optional<Message> response(Message request, ByteBufAllocator allocator) throws Exception;
}
