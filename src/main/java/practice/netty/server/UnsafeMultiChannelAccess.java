package practice.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;

public interface UnsafeMultiChannelAccess {
    ChannelPipeline pipeline(SocketAddress remoteAddress);
    Channel channel(SocketAddress remoteAddress);
    EventLoop eventLoop(SocketAddress remoteAddress);
    Thread eventLoopThread(SocketAddress remoteAddress) throws ExecutionException, InterruptedException;
}
