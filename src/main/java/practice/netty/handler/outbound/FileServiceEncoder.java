package practice.netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import practice.netty.message.MessageEncodable;
import practice.netty.specification.channel.HeaderSpecProvider;
import practice.netty.specification.message.ProtocolIdProvider;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class FileServiceEncoder extends ChannelOutboundHandlerAdapter {
    private final ProtocolIdProvider idProvider;
    private final HeaderSpecProvider headerSpecProvider;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Body 생성
        MessageEncodable encodable = (MessageEncodable) msg;
        List<EncodedPartialContents> body = encodable.encode(ctx.alloc().buffer());

        // Header 생성과 전송
        ByteBuf header = buildHeader(ctx.alloc().buffer(), body, encodable.getClass());
        ctx.write(header);

        // Body 전송
        if (isSplitBody(body)){
            // 0부터 length -1 까지의 인덱스를 가진 요소들을 순회하며 전송
            IntStream.rangeClosed(0, lastIndex(body) - 1)
                    .forEach(i -> ctx.write(body.get(i).contents()));
        }
        // 마지막 요소는 Promise 객체와 함께 전송
        ctx.write(body.get(lastIndex(body)).contents(), promise);
    }

    private ByteBuf buildHeader(ByteBuf header, List<EncodedPartialContents> messages, Class<? extends MessageEncodable> clazz) {
        // 필드 값 획득
        var length = messages.stream().mapToInt(EncodedPartialContents::length).sum() + headerSpecProvider.id().length();
        var id = idProvider.getProtocolId(clazz);
        // 버퍼에 필드 값 쓰기
        headerSpecProvider.length().writeFunc(header, length);
        headerSpecProvider.id().writeFunc(header, id);
        return header;
    }

    private static int lastIndex(List<EncodedPartialContents> body) {
        return body.size() - 1;
    }

    private static boolean isSplitBody(List<EncodedPartialContents> body) {
        return body.size() > 1;
    }
}
