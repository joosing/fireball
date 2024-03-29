package io.fireball.pipeline;

import io.fireball.eventloop.ClientEventLoopGroupManager;
import io.fireball.handler.duplex.RequestResultChecker;
import io.fireball.handler.inbound.FileStoreHandler;
import io.fireball.handler.inbound.InboundMessageValidator;
import io.fireball.handler.inbound.MessageDecoder;
import io.fireball.handler.outbound.MessageEncoder;
import io.fireball.handler.outbound.OutboundMessageValidator;
import io.fireball.handler.outbound.UserRequestHandler;
import io.fireball.specification.channel.ChannelSpecProvider;
import io.fireball.specification.message.MessageSpecProvider;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                // Duplex
                HandlerFactory.of(() -> new IdleStateHandler(0, 0, channelSpecProvider.client().idleDetectionSeconds())),
                // Outbound
                HandlerFactory.of(() -> new MessageEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new OutboundMessageValidator()),
                HandlerFactory.of(() -> new UserRequestHandler(messageSpecProvider)),
                // Inbound
                HandlerFactory.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerFactory.of(() -> new MessageDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new InboundMessageValidator()),
                HandlerFactory.of(eventLoopGroupManager.fireStore(), () -> new FileStoreHandler(channelSpecProvider.client().rootPath())), // Dedicated EventLoopGroup
                // Duplex
                HandlerFactory.of(() -> new RequestResultChecker())));
    }
}
