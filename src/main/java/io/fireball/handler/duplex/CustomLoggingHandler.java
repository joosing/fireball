package io.fireball.handler.duplex;

import io.fireball.message.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLoggingHandler extends LoggingHandler {
    private final String nextHandler;

    public CustomLoggingHandler(String nextHandler) {
        super(LogLevel.INFO);
        this.nextHandler = nextHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf buf) {
            log.info("channelRead nBytes: {} ({} before)", buf.readableBytes(), nextHandler);
        } else if (msg instanceof ProtocolMessage protocolMessage) {
            log.info("channelRead: {} ({} before)", protocolMessage.getClass().getSimpleName(), nextHandler);
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf buf) {
            log.info("write nBytes: {} ({} before)", buf.readableBytes(), nextHandler);
        } else if (msg instanceof ProtocolMessage protocolMessage) {
            log.info("write: {} ({} before)", protocolMessage.getClass().getSimpleName(), nextHandler);
        }
        ctx.write(msg, promise);
    }
}
