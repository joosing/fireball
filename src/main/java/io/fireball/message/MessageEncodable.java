package io.fireball.message;

import io.fireball.handler.outbound.EncodedBodyPiece;
import io.netty.buffer.ByteBuf;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedBodyPiece> encode(ByteBuf buffer);
}
