package practice.netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import practice.netty.message.UserMessage;
import practice.netty.specification.TxRequestProcessorProvider;

@RequiredArgsConstructor
public class UserRequestProcessHandler extends ChannelOutboundHandlerAdapter {
    private final TxRequestProcessorProvider processorProvider;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        var userMessage = (UserMessage) msg;
        var requestProcessor = processorProvider.getTxRequestProcessor(userMessage.getClass());
        var protocolMessages = requestProcessor.process(userMessage);
        protocolMessages.forEach(ctx::writeAndFlush);
    }
}
