package practice.netty.tcp;

import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpClient {
    Future<Boolean> connect(String ip, int port);

    void send(String data);

    @Nullable
    String read() throws InterruptedException;

    @Nullable
    String read(int timeout, TimeUnit unit) throws InterruptedException;

    void disconnect();

    void destroy();

    SocketAddress localAddress();

    LineBasedTcpClient.Test test();
}
