package practice.netty.tcp.server;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public interface ClientActiveListener {
    void onActive(SocketAddress remoteAddress, Channel workingChannel);
    void onInactive(SocketAddress remoteAddress);
}
