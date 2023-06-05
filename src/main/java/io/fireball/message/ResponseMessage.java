package io.fireball.message;

import io.fireball.handler.outbound.EncodedPartialContents;
import io.fireball.specification.response.ResponseSpec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class ResponseMessage implements ProtocolMessage {
    private final ResponseSpec responseSpec;

    public static ResponseMessage decode(ByteBuf message) {
        ResponseSpec responseSpec = ResponseSpec.match(message.readInt());
        return new ResponseMessage(responseSpec);
    }

    @Override
    public List<EncodedPartialContents> encode(ByteBuf buffer) {
        buffer.writeInt(responseSpec.getErrorNo());
        return List.of(new EncodedPartialContents(buffer, buffer.readableBytes()));
    }
}
