package practice.netty.tcp.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import practice.netty.tcp.common.ReadDataListener;

import java.util.List;

public interface TcpClient extends TcpClientRequest, ReadDataListener, UnsafeSingleChannelAccess  {
    void init(EventLoopGroup eventLoopGroup, List<ChannelHandler> handlers);
}
