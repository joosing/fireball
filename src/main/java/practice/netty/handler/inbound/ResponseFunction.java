package practice.netty.handler.inbound;

import io.netty.buffer.ByteBufAllocator;
import practice.netty.message.ProtocolMessage;

import java.util.List;

@FunctionalInterface
public interface ResponseFunction {
    List<ProtocolMessage> response(ProtocolMessage request, ByteBufAllocator allocator) throws Exception;
}
