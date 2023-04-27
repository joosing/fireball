package practice.netty.tcp.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.RequestProcessHandler;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.FileServiceMessageSpecProvider;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

@RequiredArgsConstructor
public class TcpFileServer extends AbstractCustomServer {
    private final FileServiceMessageSpecProvider messageSpecProvider;
    private final FileServiceChannelSpecProvider channelSpecProvider; // TODO: 인터페이스로 주입 받도록 개선합시다.

    @Override
    protected void configChildHandlers(List<HandlerWorkerPair> childHandlers) {
        // Build up
        List<HandlerWorkerPair> handlerWorkerPairs = List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(256, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                // Inbound (but it makes outbound response messages)
                HandlerWorkerPair.of(() -> new RequestProcessHandler(messageSpecProvider)));

        // Add
        childHandlers.addAll(handlerWorkerPairs);
    }
}
