package practice.netty.tcp.server;

import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpServer {
    void init();

    Future<Boolean> start(int bindPort);

    Future<?> shutdownGracefully();

    Future<Boolean> sendAll(String message);

    @Nullable
    Object read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException;

    @Nullable
    Object read(SocketAddress clientAddress) throws InterruptedException;

    boolean isActive(SocketAddress clientAddress);
}
