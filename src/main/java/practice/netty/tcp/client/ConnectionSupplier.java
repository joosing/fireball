package practice.netty.tcp.client;

import io.netty.channel.EventLoopGroup;

@FunctionalInterface
public interface ConnectionSupplier {
    CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws Exception;
}
