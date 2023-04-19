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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TcpFileServer extends AbstractCustomServer {
    private final FileServiceMessageSpecProvider messageSpecProvider;
    private final FileServiceChannelSpecProvider channelSpecProvider; // TODO: 인터페이스로 주입 받도록 개선합시다.

    @Override
    protected List<HandlerWorkerPair> configChildHandlers() {
        List<HandlerWorkerPair> childHandlers = new ArrayList<>();
        // Inbound
        childHandlers.add(
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(256, 0, 4, 0, 4)));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new InboundMessageValidator()));
        // Outbound
        childHandlers.add(
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()));
        // Inbound (but it makes outbound response messages)
        childHandlers.add(
                HandlerWorkerPair.of(() -> new RequestProcessHandler(messageSpecProvider)));

        return childHandlers;
    }
}
