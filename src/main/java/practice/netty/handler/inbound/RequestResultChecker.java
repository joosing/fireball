package practice.netty.handler.inbound;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practice.netty.message.ResponseMessage;
import practice.netty.message.UserRequest;
import practice.netty.specification.response.ResponseCode;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Getter
@Slf4j
public class RequestResultChecker extends ChannelDuplexHandler {
    private CompletableFuture<Void> responseFuture;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof UserRequest userMessage) {
            responseFuture = userMessage.responseFuture();
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseMessage response) {
            if (response.responseCode() == ResponseCode.OK) {
                responseFuture.complete(null);
            } else {
                var errorMessage = response.responseCode().getMessage();
                responseFuture.completeExceptionally(new RuntimeException(errorMessage));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An exception was thrown while processing the request on the client side.", cause);
        responseFuture.completeExceptionally(cause);
    }
}
