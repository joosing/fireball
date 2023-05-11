package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import practice.netty.specification.HeaderSpecProvider;
import practice.netty.specification.message.MessageDecoderProvider;

@RequiredArgsConstructor
public class FileServiceDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    private final MessageDecoderProvider decoderProvider;
    private final HeaderSpecProvider headerSpecProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf rawMessage) throws Exception {
        var id = headerSpecProvider.id().readFunc(rawMessage);
        var decoder = decoderProvider.getDecoder(id);
        var message = decoder.apply(rawMessage);
        ctx.fireChannelRead(message);
    }
}
