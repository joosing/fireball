package practice.netty.message;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface DecodeFunction {
    Message apply(ByteBuf message);
}
