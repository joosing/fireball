package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.netty.message.InboundFileChunk;

@RequiredArgsConstructor
@Getter
public class FileServerStoreHandler extends DedicatedSimpleInboundHandler<InboundFileChunk> {
    private final String rootPath;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundFileChunk response) throws Exception {
        var targetPath = rootPath + response.storePath();
        FileStoreAction.store(response, targetPath);
    }
}
