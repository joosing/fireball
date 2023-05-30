package io.fireball.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.FileRegion;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 파일 전송을 위한 프로토콜 메시지는 인코딩될 때 파일 내용은 FileRegion 타입을 통해 처리되고, 나머지 정보들은 ByteBuf 타입을 통해 처리됩니다.
 * 따라서 하나의 메시지를 둘로 구분할 수 있는 방법이 필요합니다. EncodedPartialContents를 사용하면 일반적인 정보는 ByteBuf로 표현하고
 * 파일 내용은 FileRegion으로 표현하여 리스트로 묶어 전송 요청 할 수 있습니다.
 */
@Getter
@Accessors(fluent = true)
public class EncodedPartialContents {
    /**
     * 인코딩되어 전송될 메시지 내용을 저장합니다. contents는 ByteBuf 또는 FileRegion 타입만 네티 채널에 의해 전송될 수 있습니다.
     */
    private final Object contents;
    /**
     * contents의 길이를 명시합니다. 만약 contents가 FileRigion 타입인 경우 최종적으로 전송될 파일의 크기를 length 필드에 저장해야 합니다.
     */
    private final int length;

    public EncodedPartialContents(Object contents, int length) {
        if (!(contents instanceof ByteBuf || contents instanceof FileRegion)) {
            throw new IllegalArgumentException("contents must be ByteBuf or FileRegion");
        }
        this.contents = contents;
        this.length = length;
    }
}
