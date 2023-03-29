package practice.netty.tcp.client;

import io.netty.channel.EventLoopGroup;

public interface CustomClient extends TcpClientRequest, UnsafeSingleChannelAccess {
    void init(EventLoopGroup eventLoopGroup);
}
