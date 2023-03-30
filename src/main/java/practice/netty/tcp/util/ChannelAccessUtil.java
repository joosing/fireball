package practice.netty.tcp.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ChannelAccessUtil {

    public static ChannelPipeline pipeline(Channel channel) {
        return channel.pipeline();
    }

    public static EventLoop eventLoop(Channel channel) {
        return channel.eventLoop();
    }

    public static Thread eventLoopThread(Channel channel) throws ExecutionException, InterruptedException {
        AtomicReference<Thread> thread = new AtomicReference<>();
        channel.eventLoop().schedule(() -> {
            thread.set(Thread.currentThread());
        }, 0, TimeUnit.MILLISECONDS).get();
        return thread.get();
    }

    private ChannelAccessUtil() {}
}
