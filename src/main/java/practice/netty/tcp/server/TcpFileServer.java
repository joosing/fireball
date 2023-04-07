package practice.netty.tcp.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.inbound.FileResponsor;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.MessageSpecProvider;
import practice.netty.tcp.common.Handler;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TcpFileServer extends AbstractCustomServer {
    private final String rootPath;
    private final MessageSpecProvider messageSpecProvider;
    private final FileServiceChannelSpecProvider channelSpecProvider; // TODO: 인터페이스로 주입 받도록 개선합시다.

    @Override
    protected List<Handler> configChildHandlers() {
        List<Handler> childHandlers = new ArrayList<>();
        // Inbound
        // childHandlers.add(Handler.of(new LoggingHandler(LogLevel.INFO))); // for debugging
        childHandlers.add(Handler.of(new LengthFieldBasedFrameDecoder(256, 0, 4, 0, 4)));
        childHandlers.add(Handler.of(new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())));
        // Outbound
        childHandlers.add(Handler.of(new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())));
        childHandlers.add(Handler.of(new OutboundMessageValidator()));
        // Inbound (but it makes outbound response messages)
        childHandlers.add(Handler.of(new FileResponsor(rootPath)));

        return childHandlers;
    }
}
