package practice.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
    public List<EncodedMessage> encode(ByteBufAllocator allocator) {
        var encodedMessage = EncodedMessage.builder()
                .message(fileContents)
                .length(fileContents.readableBytes())
                .build();
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
