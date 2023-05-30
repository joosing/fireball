package io.fireball.handler.inbound;

import io.fireball.message.ProtocolMessage;
import io.netty.buffer.ByteBufAllocator;

import java.util.List;

@FunctionalInterface
public interface ResponseFunction {
    List<ProtocolMessage> response(ProtocolMessage request, ByteBufAllocator allocator) throws Exception;
}
