package practice.netty.handler.inbound;

import io.netty.buffer.ByteBufAllocator;
import practice.netty.message.Message;

import java.util.List;

@FunctionalInterface
public interface ResponseFunction {
    List<Message> response(Message request, ByteBufAllocator allocator) throws Exception;
}
