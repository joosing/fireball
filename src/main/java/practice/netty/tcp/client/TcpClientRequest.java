package practice.netty.tcp.client;

import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpClientRequest {
    Future<Boolean> connect(String ip, int port);

    Future<Boolean> disconnect();

    Future<Boolean> send(Object data);

    @Nullable
    Object read() throws InterruptedException;

    @Nullable
    Object read(int timeout, TimeUnit unit) throws InterruptedException;

    SocketAddress localAddress();

    boolean isActive();
}
