package practice.netty.helper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import practice.netty.message.FileFetchRxResponse;
import practice.netty.specification.EncodingIdProvider;

import java.nio.charset.StandardCharsets;

public final class FileServiceHandlerTestHelper {

    public static ByteBuf buildFileFetchResponse(EmbeddedChannel channel, String fileContents,
                                                 EncodingIdProvider idProvider) {
        // 버퍼 할당
        ByteBuf buf = channel.alloc().buffer();
        // 저수준 응답 메시지 생성
        buf.writeInt(idProvider.getId(FileFetchRxResponse.class)); // id
        buf.writeCharSequence(fileContents, StandardCharsets.UTF_8); // fileContents
        return buf;
    }

    private FileServiceHandlerTestHelper() {}
}
