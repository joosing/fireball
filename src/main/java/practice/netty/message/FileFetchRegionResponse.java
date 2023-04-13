package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.DefaultFileRegion;
import lombok.Builder;

import java.io.File;
import java.util.List;

@Builder
public class FileFetchRegionResponse implements Message {
    private final boolean endOfFile;
    private final long start;
    private final long length;
    private final String filePath;

    @Override
    public List<EncodedMessage> encode(ByteBufAllocator allocator) {
        // EndOfFile
        final ByteBuf endOfFileBuf = allocator.buffer();
        endOfFileBuf.writeBoolean(endOfFile);
        var endOfFileMessage = EncodedMessage.builder()
                .message(endOfFileBuf)
                .length(1)
                .build();
        // FileRegion
        File file = new File(filePath);
        var fileRegion = EncodedMessage.builder()
                .message(new DefaultFileRegion(file, start, length))
                .length(length)
                .build();
        return List.of(endOfFileMessage, fileRegion);
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
