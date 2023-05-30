package io.fireball.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LineAppender extends ChannelOutboundHandlerAdapter {
    private final String lineSeparator;
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg + lineSeparator, promise);
    }
}
