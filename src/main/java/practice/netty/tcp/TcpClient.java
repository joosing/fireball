package practice.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;

public interface TcpClient extends DirectTcpOperation, ChannelAccessor {
    void init(EventLoopGroup eventLoopGroup, ChannelInitializer<Channel> channelInitializer);
}
