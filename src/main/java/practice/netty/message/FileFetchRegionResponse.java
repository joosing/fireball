package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultFileRegion;
import lombok.Builder;

import java.io.File;
import java.util.List;

@Builder
public class FileFetchRegionResponse implements Message {
    private final String filePath;

    @Override
    public List<EncodedMessage> encode(ByteBuf buffer) {
        File file = new File(filePath);
        var encodedMessage = EncodedMessage.builder()
                .message(new DefaultFileRegion(file, 0, file.length()))
                .length(file.length())
                .build();
        return List.of(encodedMessage);
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
