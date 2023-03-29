package practice.netty.tcp;

import io.netty.channel.EventLoopGroup;

@FunctionalInterface
public interface ConnectionSupplier {
    CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws Exception;
}
