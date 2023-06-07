package io.fireball.message;

import io.fireball.handler.outbound.EncodedBodyPiece;
import io.netty.buffer.ByteBuf;
import lombok.Builder;

import java.util.List;

@Builder
public class ChunkTransferOk implements ProtocolMessage {
    public static ChunkTransferOk decode(ByteBuf message) {
        return builder().build();
    }

    @Override
    public List<EncodedBodyPiece> encode(ByteBuf buffer) {
        return List.of(); // Empty body.
    }
}
