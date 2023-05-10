package practice.netty.tcp.server;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.inbound.FileServerStoreHandler;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.InboundRequestProcessHandler;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.specification.ChannelSpecProvider;
import practice.netty.specification.MessageSpecProvider;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

@RequiredArgsConstructor
public class TcpFileServer extends AbstractCustomServer {
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;
    private final EventLoopGroup fileStoreEventLoopGroup;

    @Override
    protected void configChildHandlers(List<HandlerWorkerPair> childHandlers) {
        // Build up
        List<HandlerWorkerPair> handlerWorkerPairs = List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(fileStoreEventLoopGroup, () -> new FileServerStoreHandler(channelSpecProvider.fileServerSpec().rootPath())),
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                // Inbound (but it makes outbound response messages)
                HandlerWorkerPair.of(() -> new InboundRequestProcessHandler(messageSpecProvider)));

        // Add
        childHandlers.addAll(handlerWorkerPairs);
    }
}
