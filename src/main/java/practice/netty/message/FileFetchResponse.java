package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class FileFetchResponse implements Message, ReferenceCounted {
    private final ByteBuf fileContents;

    public static FileFetchResponse decode(ByteBuf message) {
        ByteBuf fileContents = message.readRetainedSlice(message.readableBytes());
        return builder().fileContents(fileContents).build();
    }

    @Override
    public List<EncodedMessage> encode(ByteBuf buffer) {
        // 서버에서 FileFetchRequest 응답은 FileFetchRegionResponse를 사용해서 전송합니다. (FileRegion 특징 사용을 위해)
        // 따라서 인코딩 시도 시 예외를 던짐으로 서버에서 FileFetchResponse를 실수로 사용하는 것을 방지합니다.
        throw new IllegalStateException("The FileFetchResponse is only used for decoding by the client");
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
