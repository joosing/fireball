package practice.netty.tcp.server;

import io.netty.channel.EventLoopGroup;

public interface CustomServer extends TcpServerRequest, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup);
}
