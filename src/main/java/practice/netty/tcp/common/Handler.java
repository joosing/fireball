package practice.netty.tcp.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

public final class Handler {
    private final ChannelHandler channelHandler;
    private EventLoopGroup workGroup;

    public static Handler of(ChannelHandler channelHandler) {
        return new Handler(channelHandler);
    }

    public static Handler of(EventLoopGroup workGroup, ChannelHandler channelHandler) {
        return new Handler(workGroup, channelHandler);
    }

    private Handler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    private Handler(EventLoopGroup workGroup, ChannelHandler handler) {
        this.workGroup = workGroup;
        this.channelHandler = handler;
    }

    public EventLoopGroup workGroup() {
        return workGroup;
    }

    public ChannelHandler handler() {
        return channelHandler;
    }
}
