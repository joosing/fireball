package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultFileRegion;
import practice.netty.handler.outbound.EncodedPartialContents;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 파일을 청크 단위로 나누어 전송하기 위한 메시지입니다.
 */
public class OutboundFileChunk implements ProtocolMessage {
    // type, dstPath는 프로토콜 메시지로 직렬화됩니다.
    private final ChunkType type;
    private final String dstPath;
    // srcPath, index, length는 FileRegion을 생성하기 위한 정보입니다.
    private final String srcPath;
    private final long index;
    private final int length;

    public OutboundFileChunk(ChunkType type, String srcPath, String dstPath, long index, int length) {
        this.type = type;
        this.dstPath = dstPath;
        this.srcPath = srcPath;
        this.index = index;
        this.length = length;
    }


    @Override
    public List<EncodedPartialContents> encode(ByteBuf buffer) {
        return List.of(encodeHeader(buffer), encodeFile());
    }

    private EncodedPartialContents encodeHeader(ByteBuf buffer) {
        buffer.writeInt(type.value());
        buffer.writeInt(dstPath.length());
        buffer.writeCharSequence(dstPath, StandardCharsets.UTF_8);
        return new EncodedPartialContents(buffer, buffer.readableBytes());
    }

    private EncodedPartialContents encodeFile() {
        var fileRegion = new DefaultFileRegion(new File(srcPath), index, length);
        return new EncodedPartialContents(fileRegion, length);
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
