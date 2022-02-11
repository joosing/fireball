package practice.netty.exception.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionInboundThrowable extends ChannelInboundHandlerAdapter {
    private final Exception cause;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        throw cause;
    }
}
