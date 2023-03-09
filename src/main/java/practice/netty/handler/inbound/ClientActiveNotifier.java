package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import practice.netty.tcp.ClientActiveEventListener;

@RequiredArgsConstructor
public class ClientActiveNotifier extends ChannelInboundHandlerAdapter {
    private final ClientActiveEventListener clientActiveEventListener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clientActiveEventListener.onActive(ctx.channel().remoteAddress(), ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        clientActiveEventListener.onInactive(ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }
}
