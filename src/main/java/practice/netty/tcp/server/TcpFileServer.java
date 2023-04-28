package practice.netty.tcp.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.RequestProcessHandler;
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

    @Override
    protected void configChildHandlers(List<HandlerWorkerPair> childHandlers) {
        // Build up
        List<HandlerWorkerPair> handlerWorkerPairs = List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(256, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                // Inbound (but it makes outbound response messages)
                HandlerWorkerPair.of(() -> new RequestProcessHandler(messageSpecProvider)));

        // Add
        childHandlers.addAll(handlerWorkerPairs);
    }
}
