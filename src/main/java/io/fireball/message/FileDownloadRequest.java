package io.fireball.message;

import io.fireball.handler.outbound.EncodedBodyPiece;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Builder
@Getter
public class FileDownloadRequest implements ProtocolMessage {
    private final String srcFilePath;
    private final String dstFilePath;

    public static FileDownloadRequest decode(ByteBuf message) {
        return builder()
                .srcFilePath(message.readSlice(message.readInt()).toString(StandardCharsets.UTF_8))
                .dstFilePath(message.readSlice(message.readInt()).toString(StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public List<EncodedBodyPiece> encode(ByteBuf buffer) {
        buffer.writeInt(srcFilePath.length());
        buffer.writeCharSequence(srcFilePath, StandardCharsets.UTF_8);
        buffer.writeInt(dstFilePath.length());
        buffer.writeCharSequence(dstFilePath, StandardCharsets.UTF_8);
        return List.of(new EncodedBodyPiece(buffer, buffer.readableBytes()));
    }

    @Override
    public void validate() throws Exception {
        // TODO: 입력된 경로의 포멧을 검증하는 로직을 추가 해야합니다.
    }
}
