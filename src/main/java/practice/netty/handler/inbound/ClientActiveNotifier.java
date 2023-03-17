package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import practice.netty.tcp.ClientActiveListener;

@RequiredArgsConstructor
public class ClientActiveNotifier extends ChannelInboundHandlerAdapter {
    private final ClientActiveListener clientActiveListener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clientActiveListener.onActive(ctx.channel().remoteAddress(), ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        clientActiveListener.onInactive(ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }
}
