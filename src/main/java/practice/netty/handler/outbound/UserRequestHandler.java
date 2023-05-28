package practice.netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import practice.netty.message.UserRequest;
import practice.netty.specification.message.OutboundRequestProcessorProvider;

@RequiredArgsConstructor
public class UserRequestHandler extends ChannelOutboundHandlerAdapter {
    private final OutboundRequestProcessorProvider processorProvider;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        var userMessage = (UserRequest) msg;
        var requestProcessor = processorProvider.getOutboundRequestProcessor(userMessage.getClass());
        var messages = requestProcessor.process(userMessage);
        messages.forEach(ctx::writeAndFlush);
    }
}
