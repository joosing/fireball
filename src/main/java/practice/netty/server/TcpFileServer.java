package practice.netty.server;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.common.HandlerFactory;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.InboundRequestHandler;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;

import java.util.List;

@RequiredArgsConstructor
public class TcpFileServer extends AbstractCustomServer {
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;
    private final EventLoopGroup fileStoreEventLoopGroup;

    @Override
    protected void configChildHandlers(List<HandlerFactory> pipelineFactory) {
        // Build up
        List<HandlerFactory> handlerWorkerPairs = List.of(
                // Inbound
                HandlerFactory.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerFactory.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new InboundMessageValidator()),
                HandlerFactory.of(fileStoreEventLoopGroup, () -> new FileStoreHandler(channelSpecProvider.server().rootPath())),
                // Outbound
                HandlerFactory.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new OutboundMessageValidator()),
                // Inbound (but it makes outbound response messages)
                HandlerFactory.of(() -> new InboundRequestHandler(messageSpecProvider)));

        // Add
        pipelineFactory.addAll(handlerWorkerPairs);
    }
}
