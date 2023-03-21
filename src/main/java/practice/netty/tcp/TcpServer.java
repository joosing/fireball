package practice.netty.tcp;

import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpServer {
    Future<Boolean> start(int bindPort);

    void destroy();

    void sendAll(String message);

    @Nullable
    String read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException;

    @Nullable
    String read(SocketAddress clientAddress) throws InterruptedException;

    boolean isActive(SocketAddress clientAddress);
}
