package io.fireball.handler.duplex;

import io.fireball.message.ResponseMessage;
import io.fireball.message.UserRequest;
import io.fireball.specification.response.ResponseCode;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        responseFuture.completeExceptionally(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.ALL_IDLE) {
                responseFuture.completeExceptionally(new RuntimeException("The server is not responding."));
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        responseFuture.completeExceptionally(new RuntimeException("Connection is closed."));
        super.channelUnregistered(ctx);
    }
}
