package io.fireball.message;

import io.fireball.handler.outbound.EncodedBodyPiece;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 파일을 청크 단위로 나누어 수신하기 위한 메시지입니다.
 */
@Builder
@Getter
@Accessors(fluent = true)
public class InboundFileChunk implements ProtocolMessage, ReferenceCounted {
    private final ChunkType type;
    private final String storePath;
    private final ByteBuf contents;

    public static InboundFileChunk decode(ByteBuf message) {
        int chunkType = message.readInt();
        String storePath = message.readCharSequence(message.readInt(), StandardCharsets.UTF_8).toString();
        ByteBuf fileContents = message.readRetainedSlice(message.readableBytes());
        return builder()
                .type(ChunkType.of(chunkType))
                .storePath(storePath)
                .contents(fileContents)
                .build();
    }

    @Override
    public List<EncodedBodyPiece> encode(ByteBuf buffer) {
        buffer.writeInt(type.value());
        buffer.writeInt(storePath.length());
        buffer.writeCharSequence(storePath, StandardCharsets.UTF_8);
        buffer.writeBytes(contents);
        var encodedMessage = new EncodedBodyPiece(buffer, buffer.readableBytes());
        contents.release();
        return List.of(encodedMessage);
    }

    @Override
    public int refCnt() {
        return contents.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        contents.retain();
        return this;
    }

    @Override
    public ReferenceCounted retain(int increment) {
        contents.retain(increment);
        return this;
    }

    /**
     * 네티 채널 파이프라인에서 ReferenceCounted 인터페이스를 구현한 객체를 다루는 경우 항상 touch()를 통해 반환된 객체를 사용합니다.
     * 따라서 여기서 this를 반환해야 하며 fileContents를 반환하면 파이프라인 처리에 오류가 발생합니다.
     */
    @Override
    public ReferenceCounted touch() {
        return this;
    }

    /**
     * 네티 채널 파이프라인에서 ReferenceCounted 인터페이스를 구현한 객체를 다루는 경우 항상 touch()를 통해 반환된 객체를 사용합니다.
     * 따라서 여기서 this를 반환해야 하며 fileContents를 반환하면 파이프라인 처리에 오류가 발생합니다.
     */
    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return contents.release();
    }

    @Override
    public boolean release(int decrement) {
        return contents.release(decrement);
    }
}
