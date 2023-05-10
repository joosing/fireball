package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import practice.netty.message.InboundFileChunk;

@RequiredArgsConstructor
public class FileClientStoreHandler extends DedicatedSimpleInboundHandler<InboundFileChunk> {
    private final String storePath;
    private final Runnable storeCompleteAction;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundFileChunk response) throws Exception {
        FileStoreAction.store(response, storePath, storeCompleteAction);
    }
}
