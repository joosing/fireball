package practice.netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import practice.netty.message.MessageValidatable;

public class OutboundMessageValidator extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        var message = (MessageValidatable) msg;
        message.validate();
        ctx.write(msg, promise);
    }
}
