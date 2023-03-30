package practice.netty.tcp.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import practice.netty.tcp.common.ReadDataListener;

import java.util.List;
import java.util.function.Supplier;

public interface TcpServer extends TcpServerRequest, ReadDataListener, ClientActiveListener, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup, List<Supplier<ChannelHandler>> childHandlers);
}
