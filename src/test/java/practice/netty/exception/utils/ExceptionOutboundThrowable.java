package practice.netty.exception.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionOutboundThrowable extends ChannelOutboundHandlerAdapter {
    private final Exception cause;
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        throw cause;
    }
}
