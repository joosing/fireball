package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.DefaultFileRegion;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.outbound.EncodedSubMessage;

import java.io.File;
import java.util.List;

/**
 * 파일 패치 요청에 대한 응답 메시지입니다. 이 클래스는 서버측에서 응답을 전송하기 위해 필요한 정보를 담고 있습니다.
 */
@RequiredArgsConstructor
public class FileChunkTxResponse implements Message {
    private final ChunkType chunkType;
    private final long start;
    private final int length;
    private final String filePath;

    @Override
    public List<EncodedSubMessage> encode(ByteBufAllocator allocator) {
        return List.of(encodeChunkType(allocator), encodeFileRegion(allocator));
    }

    private EncodedSubMessage encodeChunkType(ByteBufAllocator allocator) {
        final ByteBuf buffer = allocator.buffer();
        buffer.writeInt(chunkType.value());
        return new EncodedSubMessage(buffer, buffer.readableBytes());
    }

    private EncodedSubMessage encodeFileRegion(ByteBufAllocator allocator) {
        File file = new File(filePath);
        return new EncodedSubMessage(new DefaultFileRegion(file, start, length), length);
    }

    @Override
    public void validate() throws Exception {
        File file = new File(filePath);
        // 전송 가능한 최대 파일 크기 확인
        if (file.length() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large file: " + file.length() + " bytes");
        }
    }
}
