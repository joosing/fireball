package practice.netty.tcp;

import io.netty.channel.Channel;

@FunctionalInterface
public interface ChannelAccessor {
    Channel channel();
}
