package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCounted;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 파일 패치 요청에 대한 응답 메시지입니다. 이 클래스는 클라이언트 측에서 응답을 수신하여 처리하기 위한 정보를 담고 있습니다.
 */
@Builder
@Getter
public class FileFetchRxResponse implements Message, ReferenceCounted {
    private boolean endOfFile;
    private ByteBuf fileContents;

    public static FileFetchRxResponse decode(ByteBuf message) {
        boolean endOfFile = message.readBoolean();
        ByteBuf fileContents = message.readRetainedSlice(message.readableBytes());
        return builder()
                .endOfFile(endOfFile)
                .fileContents(fileContents)
                .build();
    }

    @Override
    public List<EncodedSubMessage> encode(ByteBufAllocator allocator) {
        final ByteBuf buffer = allocator.directBuffer();
        buffer.writeBoolean(endOfFile);
        buffer.writeBytes(fileContents);
        var encodedMessage = EncodedSubMessage.builder()
                .message(buffer)
                .length(1 + fileContents.readableBytes()) // endOfFile size + fileContents size
                .build();
        fileContents.release();
        return List.of(encodedMessage);
    }

    @Override
    public int refCnt() {
        return fileContents.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        return fileContents.retain();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return fileContents.retain(increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return fileContents.release();
    }

    @Override
    public boolean release(int decrement) {
        return fileContents.release(decrement);
    }
}
