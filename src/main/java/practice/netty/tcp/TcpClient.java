package practice.netty.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface TcpClient {
    void init(EventLoopGroup eventLoopGroup);

    void init();

    Future<Boolean> connect(String ip, int port);

    void disconnect();

    Future<?> shutdownGracefully();

    ChannelFuture send(String data);

    @Nullable
    String read() throws InterruptedException;

    @Nullable
    String read(int timeout, TimeUnit unit) throws InterruptedException;

    SocketAddress localAddress();

    LineBasedTcpClient.Test test();
}
