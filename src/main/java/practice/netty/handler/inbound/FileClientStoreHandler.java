package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import practice.netty.message.FileRxChunk;

@RequiredArgsConstructor
public class FileClientStoreHandler extends DedicatedSimpleInboundHandler<FileRxChunk> {
    private final String storePath;
    private final Runnable storeCompleteAction;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileRxChunk response) throws Exception {
        FileStoreAction.store(response, storePath, storeCompleteAction);
    }
}