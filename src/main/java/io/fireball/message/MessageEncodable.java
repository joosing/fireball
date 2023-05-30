package io.fireball.message;

import io.fireball.handler.outbound.EncodedPartialContents;
import io.netty.buffer.ByteBuf;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedPartialContents> encode(ByteBuf buffer);
}
