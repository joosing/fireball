package practice.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

public interface CustomClient extends DirectTcpOperation, ChannelTestable {
    void init(EventLoopGroup eventLoopGroup);
    void init(EventLoopGroup eventLoopGroup, ChannelInitializer<Channel> channelInitializer);
    @Nullable
    String read() throws InterruptedException;
    @Nullable
    String read(int timeout, TimeUnit unit) throws InterruptedException;
}
