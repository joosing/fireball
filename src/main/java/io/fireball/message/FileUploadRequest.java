package io.fireball.message;

import io.fireball.handler.outbound.EncodedPartialContents;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Builder
@Getter
public class FileUploadRequest implements ProtocolMessage {
    private final String srcFilePath;
    private final String dstFilePath;

    public static FileUploadRequest decode(ByteBuf message) {
        return builder()
                .srcFilePath(message.readSlice(message.readInt()).toString(StandardCharsets.UTF_8))
                .dstFilePath(message.readSlice(message.readInt()).toString(StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public List<EncodedPartialContents> encode(ByteBuf buffer) {
        buffer.writeInt(srcFilePath.length());
        buffer.writeCharSequence(srcFilePath, StandardCharsets.UTF_8);
        buffer.writeInt(dstFilePath.length());
        buffer.writeCharSequence(dstFilePath, StandardCharsets.UTF_8);
        return List.of(new EncodedPartialContents(buffer, buffer.readableBytes()));
    }
}
