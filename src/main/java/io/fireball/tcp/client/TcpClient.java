package io.fireball.tcp.client;

import io.fireball.handler.inbound.ReadDataListener;
import io.fireball.pipeline.HandlerFactory;
import io.netty.channel.EventLoopGroup;

import java.util.List;

public interface TcpClient extends TcpClientRequest, ReadDataListener, UnsafeSingleChannelAccess  {
    void init(EventLoopGroup eventLoopGroup, List<HandlerFactory> pipelineFactory);
}
