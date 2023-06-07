package io.fireball.handler.inbound;

import io.fireball.message.ChunkTransferOk;
import io.fireball.message.InboundFileChunk;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
@Getter
public class FileStoreHandler extends DedicatedSimpleInboundHandler<InboundFileChunk> {
    private final String rootPath;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundFileChunk chunk) throws Exception {
        var targetPath = Path.of(rootPath, chunk.storePath()).normalize().toString();
        FileStoreAction.store(chunk, targetPath);
        ctx.writeAndFlush(ChunkTransferOk.builder().build());
    }
}
