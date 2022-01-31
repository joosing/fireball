package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class ReceiveDataUpdater extends SimpleChannelInboundHandler<String> {
    private final BlockingQueue<String> messageQueue;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        messageQueue.put(msg);
        ctx.fireChannelRead(msg);
    }
}
