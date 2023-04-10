package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import practice.netty.message.MessageValidatable;

public class InboundMessageValidator extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageValidatable validatable = (MessageValidatable) msg;
        validatable.validate();
        ctx.fireChannelRead(msg);
    }
}
