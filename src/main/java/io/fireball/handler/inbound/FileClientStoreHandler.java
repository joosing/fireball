package io.fireball.handler.inbound;

import io.fireball.message.InboundFileChunk;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileClientStoreHandler extends DedicatedSimpleInboundHandler<InboundFileChunk> {
    private final String storePath;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundFileChunk response) throws Exception {
        FileStoreAction.store(response, storePath);
    }
}
