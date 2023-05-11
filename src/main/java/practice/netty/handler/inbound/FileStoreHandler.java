package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.netty.message.InboundFileChunk;

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
