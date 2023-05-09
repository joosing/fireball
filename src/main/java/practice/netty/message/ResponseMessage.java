package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import practice.netty.handler.outbound.EncodedSubMessage;
import practice.netty.specification.ResponseCode;

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
    public List<EncodedSubMessage> encode(ByteBufAllocator allocator) {
        var buffer = allocator.buffer();
        buffer.writeInt(responseCode.getCode());
        return List.of(new EncodedSubMessage(buffer, buffer.readableBytes()));
    }
}
