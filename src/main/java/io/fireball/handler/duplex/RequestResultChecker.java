package io.fireball.handler.duplex;

import io.fireball.message.ResponseMessage;
import io.fireball.specification.response.ResponseCode;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * A request is considered complete when it receives a response.
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@Slf4j
public class RequestResultChecker extends ChannelDuplexHandler {
    private final CompletableFuture<Void> completableFuture = new CompletableFuture<>();

    /**
     * Process the received response message.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseMessage response) {
            if (response.responseCode() == ResponseCode.OK) {
                completableFuture.complete(null);
            } else {
                var errorMessage = response.responseCode().getMessage();
                completableFuture.completeExceptionally(new RuntimeException(errorMessage));
            }
        }
    }

    /**
     * Handles exceptions thrown during request processing.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        completableFuture.completeExceptionally(cause);
    }

    /**
     * Handles the Idle state where there is no response from the server.
     * This event is triggered by the IdleStateHandler.
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.ALL_IDLE) {
                completableFuture.completeExceptionally(new RuntimeException("The server is not responding."));
            }
        }
    }

    /**
     * Handle cases where the connection is closed.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        completableFuture.completeExceptionally(new RuntimeException("The channel get closed."));
        super.channelInactive(ctx);
    }
}
