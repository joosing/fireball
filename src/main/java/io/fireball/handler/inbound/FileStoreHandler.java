package io.fireball.handler.inbound;

import io.fireball.message.InboundFileChunk;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FileStoreHandler extends DedicatedSimpleInboundHandler<InboundFileChunk> {
    private final String rootPath;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundFileChunk chunk) throws Exception {
        var targetPath = rootPath + chunk.storePath();
        FileStoreAction.store(chunk, targetPath);
    }
}
