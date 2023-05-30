package io.fireball.tcp.client;

import io.netty.channel.ChannelFuture;
import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public interface TcpClientRequest {
    ChannelFuture connect(String ip, int port) throws InterruptedException;

    ChannelFuture disconnect();

    ChannelFuture send(Object data);

    Object readSync() throws InterruptedException;

    @Nullable
    Object read(int timeout, TimeUnit unit) throws InterruptedException;

    SocketAddress localAddress();

    boolean isActive();
}
