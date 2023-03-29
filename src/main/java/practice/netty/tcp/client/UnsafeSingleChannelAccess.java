package practice.netty.tcp.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.util.concurrent.ExecutionException;

public interface UnsafeSingleChannelAccess {
    ChannelPipeline pipeline();
    Channel channel();
    EventLoop eventLoop();
    Thread eventLoopThread() throws ExecutionException, InterruptedException;
}
