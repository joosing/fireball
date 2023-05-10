package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Builder;
import lombok.Getter;
import practice.netty.handler.outbound.EncodedSubMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Builder
@Getter
public class FileDownloadRequest implements ProtocolMessage {
    private final String remoteFilePath;

    public static FileDownloadRequest decode(ByteBuf message) {
        String remoteFilePath = message.readSlice(message.readableBytes()).toString(StandardCharsets.UTF_8); // retain() 필요 없음
        return builder().remoteFilePath(remoteFilePath).build();
    }

    @Override
    public List<EncodedSubMessage> encode(ByteBufAllocator allocator) {
        ByteBuf buffer = allocator.buffer();
        buffer.writeCharSequence(remoteFilePath, StandardCharsets.UTF_8);
        return List.of(new EncodedSubMessage(buffer, remoteFilePath.length()));
    }

    @Override
    public void validate() throws Exception {
        // TODO: 입력된 경로의 포멧을 검증하는 로직을 추가 해야합니다.
    }
}
