package practice.netty.study.sharable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class SharableThreadUnsafeCountingHandler extends ChannelOutboundHandlerAdapter {
    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {
        count++;
        super.write(ctx, msg, promise);
    }
}
