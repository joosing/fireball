package practice.netty.pipeline;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.netty.common.HandlerFactory;
import practice.netty.eventloop.ClientEventLoopGroupManager;
import practice.netty.handler.inbound.CompleteResponseNotifier;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.handler.outbound.UserRequestHandler;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component("tcpFileClientPipelineFactory")
public class TcpFileClientPipelineFactory implements PipelineFactory {
    private final ClientEventLoopGroupManager eventLoopGroupManager;
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;

    @Override
    public List<HandlerFactory> get() {
        return new ArrayList<>(List.of(
                // Inbound
                HandlerFactory.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerFactory.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new InboundMessageValidator()),
                HandlerFactory.of(eventLoopGroupManager.fireStore(), () -> new FileStoreHandler(channelSpecProvider.client().rootPath())), // Dedicated EventLoopGroup
                // Outbound
                HandlerFactory.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new OutboundMessageValidator()),
                HandlerFactory.of(() -> new UserRequestHandler(messageSpecProvider)),
                // Duplex
                HandlerFactory.of(() -> new CompleteResponseNotifier())));
    }
}
