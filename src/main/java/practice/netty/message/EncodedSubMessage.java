package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.FileRegion;
import lombok.Getter;

@Getter
public class EncodedSubMessage {
    /**
     * 인코딩되어 전송될 메시지 내용을 저장합니다. contents는 ByteBuf 또는 FileRegion 타입만 네티 채널에 의해 전송될 수 있습니다.
     */
    private final Object message;
    /**
     * contents의 길이를 명시합니다. 만약 contents가 FileRigion 타입인 경우 최종적으로 전송될 파일의 크기를 length 필드에 저장해야 합니다.
     */
    private final long length;

    public EncodedSubMessage(Object message, long length) {
        if (!(message instanceof ByteBuf || message instanceof FileRegion)) {
            throw new IllegalArgumentException("message must be ByteBuf or FileRegion");
        }
        this.message = message;
        this.length = length;
    }
}
