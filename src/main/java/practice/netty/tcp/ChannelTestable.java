package practice.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.util.concurrent.ExecutionException;

public interface ChannelTestable {
    ChannelPipeline pipeline();
    Channel channel();
    EventLoop eventLoop();
    Thread eventLoopThread() throws ExecutionException, InterruptedException;
}
