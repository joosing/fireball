package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.netty.message.ChunkType;
import practice.netty.message.FileRxChunk;

@RequiredArgsConstructor
@Getter
public class FileServerStoreHandler extends DedicatedSimpleInboundHandler<FileRxChunk> {
    private final String rootPath;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileRxChunk response) throws Exception {
        var targetPath = rootPath + response.storePath();
        FileStoreAction.store(response, targetPath, null);
        if (response.chunkType() == ChunkType.END_OF_FILE) {
            ctx.fireChannelRead(response.retain());
        }
    }
}
