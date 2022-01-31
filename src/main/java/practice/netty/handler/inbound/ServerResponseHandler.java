package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerResponseHandler extends SimpleChannelInboundHandler<String> {
    private final String fixedResponse;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        ctx.fireChannelRead(msg);
        ctx.channel().writeAndFlush(fixedResponse);
    }
}
