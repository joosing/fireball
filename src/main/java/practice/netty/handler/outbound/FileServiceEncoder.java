package practice.netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import practice.netty.message.MessageEncodable;
import practice.netty.specification.EncodingIdProvider;
import practice.netty.specification.HeaderSpecProvider;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class FileServiceEncoder extends ChannelOutboundHandlerAdapter {
    private final EncodingIdProvider idProvider;
    private final HeaderSpecProvider headerSpecProvider;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Body 생성
        MessageEncodable encodable = (MessageEncodable) msg;
        List<EncodedSubMessage> body = encodable.encode(ctx.alloc().buffer());

        // Header 생성과 전송
        ByteBuf header = buildHeader(ctx.alloc().buffer(), body, encodable.getClass());
        ctx.write(header);

        // Body 전송
        if (composedBody(body)){
            IntStream.rangeClosed(0, lastIndex(body) - 1)
                    .forEach(i -> ctx.write(body.get(i).subMessage()));
        }
        ctx.write(body.get(lastIndex(body)).subMessage(), promise);
    }

    private ByteBuf buildHeader(ByteBuf header, List<EncodedSubMessage> messages, Class<? extends MessageEncodable> clazz) {
        // 필드 값 획득
        var length = messages.stream().mapToInt(EncodedSubMessage::length).sum() + headerSpecProvider.id().length();
        var id = idProvider.getId(clazz);
        // 버퍼에 필드 값 쓰기
        headerSpecProvider.length().writeFunc(header, length);
        headerSpecProvider.id().writeFunc(header, id);
        return header;
    }

    private static int lastIndex(List<EncodedSubMessage> body) {
        return body.size() - 1;
    }

    private static boolean composedBody(List<EncodedSubMessage> body) {
        return body.size() > 1;
    }
}
