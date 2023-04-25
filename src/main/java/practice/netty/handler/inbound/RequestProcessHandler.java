package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practice.netty.message.Message;
import practice.netty.specification.RequestProcessorProvider;

@Slf4j
@RequiredArgsConstructor
public class RequestProcessHandler extends SimpleChannelInboundHandler<Message> {
    private final RequestProcessorProvider requestProcessorProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        var requestProcessor = requestProcessorProvider.getRequestProcessor(message.getClass());
        var responses = requestProcessor.process(message);
        responses.forEach(ctx::writeAndFlush);
    }
}
