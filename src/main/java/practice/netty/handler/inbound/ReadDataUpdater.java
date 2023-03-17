package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import practice.netty.tcp.ReadableQueueListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
@RequiredArgsConstructor
public class ReadDataUpdater extends SimpleChannelInboundHandler<String> {
    private final ReadableQueueListener readableQueueListener;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        messageQueue.put(msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 주의: 여기서 큐를 초기화하지 않습니다. 연결이 끊어졌지만 외부에서 이미 수신하여 큐에 쌓여있는 데이터를 처리할 수 있는 기회를 줍니다.
        readableQueueListener.onReadAvailable(ctx.channel().remoteAddress(), messageQueue);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        messageQueue.clear();
        readableQueueListener.onReadUnavailable(ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
