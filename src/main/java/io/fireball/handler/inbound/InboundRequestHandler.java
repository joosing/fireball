package io.fireball.handler.inbound;

import io.fireball.message.ProtocolMessage;
import io.fireball.message.ResponseMessage;
import io.fireball.specification.message.InboundRequestProcessorProvider;
import io.fireball.specification.response.ResponseSpec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InboundRequestHandler extends SimpleChannelInboundHandler<ProtocolMessage> {
    private final InboundRequestProcessorProvider processorProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage message) {
        try {
            var requestProcessor = processorProvider.getInboundRequestProcessor(message.getClass());
            // 응답 생성
            var responses = requestProcessor.process(message);
            responses.add(new ResponseMessage(ResponseSpec.OK));
            // 응답 전송
            responses.forEach(response -> {
                ctx.writeAndFlush(response).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("Failed to send a response.", future.cause());
                    }
                });
            });
        } catch (Throwable throwable) {
            handleException(ctx, throwable);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleException(ctx, cause);
    }

    private static void handleException(ChannelHandlerContext ctx, Throwable cause) {
        log.error("An exception was thrown while processing the request on the server side.", cause);
        ctx.writeAndFlush(new ResponseMessage(ResponseSpec.match(cause)));
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.ALL_IDLE) {
                log.error("The client({}) channel is idle.", ctx.channel().remoteAddress());
                ctx.close();
            }
        }
    }
}
