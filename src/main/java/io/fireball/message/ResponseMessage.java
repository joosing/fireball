package io.fireball.message;

import io.fireball.handler.outbound.EncodedPartialContents;
import io.fireball.specification.response.ResponseCode;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class ResponseMessage implements ProtocolMessage {
    private final ResponseCode responseCode;

    public static ResponseMessage decode(ByteBuf message) {
        ResponseCode responseCode = ResponseCode.match(message.readInt());
        return new ResponseMessage(responseCode);
    }

    @Override
    public List<EncodedPartialContents> encode(ByteBuf buffer) {
        buffer.writeInt(responseCode.getCode());
        return List.of(new EncodedPartialContents(buffer, buffer.readableBytes()));
    }
}
