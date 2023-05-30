package io.fireball.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class ReadDataUpdater extends SimpleChannelInboundHandler<Object> {
    private final ReadDataListener readDataListener;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        readDataListener.onReadAvailable(ctx.channel().remoteAddress(), msg);
        ctx.fireChannelRead(msg);
    }
}
