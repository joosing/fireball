package practice.netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import practice.netty.message.EncodedSubMessage;
import practice.netty.message.MessageEncodable;
import practice.netty.specification.EncodingIdProvider;
import practice.netty.specification.FileServiceChannelSpecProvider;

import java.util.List;

@RequiredArgsConstructor
public class FileServiceEncoder extends ChannelOutboundHandlerAdapter {
    private final EncodingIdProvider idProvider;
    private final FileServiceChannelSpecProvider.HeaderSpec headerSpecProvider;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Body 생성
        MessageEncodable encodable = (MessageEncodable) msg;
        List<EncodedSubMessage> body = encodable.encode(ctx.alloc());

        // Header 생성 및 전송
        ByteBuf header = buildHeader(ctx, body, encodable.getClass());
        ctx.write(header);
        // Body 전송
        if (body.size() != 1){
            for (int i = 0; i < body.size() - 1; i++) {
                ctx.write(body.get(i).getMessage());
            }
        }
        ctx.write(body.get(body.size() - 1).getMessage(), promise);
    }

    private ByteBuf buildHeader(ChannelHandlerContext ctx,
                                List<EncodedSubMessage> messages, Class<? extends MessageEncodable> clazz) {
        // 헤더 버퍼
        var header = ctx.alloc().buffer();
        // 필드 값 획득
        var length = messages.stream().mapToLong(EncodedSubMessage::getLength).sum() + headerSpecProvider.id().length();
        var id = idProvider.getId(clazz);
        // 버퍼에 필드 값 쓰기
        headerSpecProvider.length().write(header, (int) length);
        headerSpecProvider.id().write(header, id);
        return header;
    }
}
