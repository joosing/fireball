package io.fireball.tcp.server;

import io.fireball.handler.inbound.ReadDataListener;
import io.fireball.pipeline.HandlerFactory;
import io.netty.channel.EventLoopGroup;

import java.util.List;

public interface TcpServer extends TcpServerRequest, ReadDataListener, ClientActiveListener, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup, List<HandlerFactory> childHandlers);
}
