package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import practice.netty.tcp.ReceiveAvailableListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
@RequiredArgsConstructor
public class ReceiveDataUpdater extends SimpleChannelInboundHandler<String> {
    private final ReceiveAvailableListener receiveAvailableListener;
    @Nullable private BlockingQueue<String> messageQueue;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        assert messageQueue != null;
        messageQueue.put(msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        messageQueue = new LinkedBlockingQueue<>();
        receiveAvailableListener.onReceiveAvailable(ctx.channel().remoteAddress(), messageQueue);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        receiveAvailableListener.onReceiveUnavailable(ctx.channel().remoteAddress());
        assert messageQueue != null;
        messageQueue.clear();
        messageQueue = null;
        super.channelInactive(ctx);
    }
}
