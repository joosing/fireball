package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.DefaultFileRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import practice.netty.handler.outbound.EncodedSubMessage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 파일을 청크 단위로 나누어 전송하기 위한 메시지입니다.
 */
@RequiredArgsConstructor
public class OutboundFileChunk implements ProtocolMessage {
    private final ChunkType type;
    @Nullable private final String dstPath;
    private final String srcPath;
    private final long startIndex;
    private final int length;

    @Override
    public List<EncodedSubMessage> encode(ByteBufAllocator allocator) {
        return List.of(encodeHeader(allocator), encodeFile(allocator));
    }

    private EncodedSubMessage encodeHeader(ByteBufAllocator allocator) {
        final ByteBuf buffer = allocator.buffer();
        buffer.writeInt(type.value());
        if (dstPath != null) {
            buffer.writeInt(dstPath.length());
            buffer.writeCharSequence(dstPath, StandardCharsets.UTF_8);
        } else {
            buffer.writeInt(0);
        }
        return new EncodedSubMessage(buffer, buffer.readableBytes());
    }

    private EncodedSubMessage encodeFile(ByteBufAllocator allocator) {
        File file = new File(srcPath);
        return new EncodedSubMessage(new DefaultFileRegion(file, startIndex, length), length);
    }

    @Override
    public void validate() throws Exception {
        File file = new File(srcPath);
        // 전송 가능한 최대 파일 크기 확인
        if (file.length() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large file: " + file.length() + " bytes");
        }
    }
}
