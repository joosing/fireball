package io.fireball.handler.inbound;

import io.fireball.message.MessageValidatable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundMessageValidator extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageValidatable validatable = (MessageValidatable) msg;
        validatable.validate();
        ctx.fireChannelRead(msg);
    }
}
