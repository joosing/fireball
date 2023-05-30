package io.fireball.handler.outbound;

import io.fireball.message.MessageValidatable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundMessageValidator extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        var message = (MessageValidatable) msg;
        message.validate();
        ctx.write(msg, promise);
    }
}
