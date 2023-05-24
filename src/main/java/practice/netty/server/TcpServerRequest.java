package practice.netty.server;

import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpServerRequest {

    Future<Boolean> start(int bindPort);

    Future<Boolean> sendAll(Object message);

    @Nullable
    Object read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException;

    Object readSync(SocketAddress clientAddress) throws InterruptedException;

    boolean isActive(SocketAddress clientAddress);
}
