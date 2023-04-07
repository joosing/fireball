package practice.netty.study.sharable;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Sharable
public class SharableCountingHandler extends ChannelOutboundHandlerAdapter {
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
