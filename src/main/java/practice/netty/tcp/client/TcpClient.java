package practice.netty.tcp.client;

import io.netty.channel.EventLoopGroup;
import practice.netty.handler.inbound.ReadDataListener;
import practice.netty.pipeline.HandlerFactory;

import java.util.List;

public interface TcpClient extends TcpClientRequest, ReadDataListener, UnsafeSingleChannelAccess  {
    void init(EventLoopGroup eventLoopGroup, List<HandlerFactory> pipelineFactory);
}
