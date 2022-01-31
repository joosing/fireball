package practice.netty.handler.inbound;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ActiveServerChannelUpdater extends ChannelInboundHandlerAdapter {
    private final ConcurrentHashMap<SocketAddress, Channel> activeServerChannelMap;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        activeServerChannelMap.put(ctx.channel().remoteAddress(), ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        activeServerChannelMap.remove(ctx.channel().remoteAddress(), ctx.channel());
        ctx.fireChannelInactive();
    }
}
