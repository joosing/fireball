package practice.netty.study.sharable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.atomic.AtomicInteger;

public class UnsharableCountingHandler extends ChannelOutboundHandlerAdapter {
    private final AtomicInteger count = new AtomicInteger(0);

    public int getCount() {
        return count.get();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        count.incrementAndGet();
        super.write(ctx, msg, promise);
    }
}
