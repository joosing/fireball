package io.fireball.message;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface DecodeFunction {
    ProtocolMessage apply(ByteBuf message);
}
