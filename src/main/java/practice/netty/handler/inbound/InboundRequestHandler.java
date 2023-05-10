package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.ResponseMessage;
import practice.netty.specification.InboundRequestProcessorProvider;
import practice.netty.specification.ResponseCode;

@Slf4j
@RequiredArgsConstructor
public class InboundRequestHandler extends SimpleChannelInboundHandler<ProtocolMessage> {
    private final InboundRequestProcessorProvider requestProcessorProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage message) {
        try {
            var requestProcessor = requestProcessorProvider.getInboundRequestProcessor(message.getClass());
            var responses = requestProcessor.process(message);
            responses.forEach(ctx::writeAndFlush);
        } catch (Throwable throwable) {
            ctx.writeAndFlush(new ResponseMessage(ResponseCode.match(throwable)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(new ResponseMessage(ResponseCode.match(cause)));
        log.error("Exception occurred while processing request", cause);
    }
}
