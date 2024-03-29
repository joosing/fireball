package io.fireball.pipeline;

import io.fireball.eventloop.ServerEventLoopGroupManager;
import io.fireball.handler.inbound.FileStoreHandler;
import io.fireball.handler.inbound.InboundMessageValidator;
import io.fireball.handler.inbound.InboundRequestHandler;
import io.fireball.handler.inbound.MessageDecoder;
import io.fireball.handler.outbound.MessageEncoder;
import io.fireball.handler.outbound.OutboundMessageValidator;
import io.fireball.specification.channel.ChannelSpecProvider;
import io.fireball.specification.message.MessageSpecProvider;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component("tcpFileServerPipelineFactory")
public class TcpFileServerPipelineFactory implements PipelineFactory {
    private final ServerEventLoopGroupManager eventLoopGroupManager;
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;

    @Override
    public List<HandlerFactory> get() {
        return new ArrayList<>(List.of(
                // Duplex
                HandlerFactory.of(() -> new IdleStateHandler(0, 0, channelSpecProvider.server().idleDetectionSeconds())),
                // Outbound
                HandlerFactory.of(() -> new MessageEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new OutboundMessageValidator()),
                // Inbound
                HandlerFactory.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerFactory.of(() -> new MessageDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerFactory.of(() -> new InboundMessageValidator()),
                HandlerFactory.of(eventLoopGroupManager.fireStore(), () -> new FileStoreHandler(channelSpecProvider.server().rootPath())),
                // Inbound (but it makes outbound response messages)
                HandlerFactory.of(() -> new InboundRequestHandler(messageSpecProvider))));
    }
}
