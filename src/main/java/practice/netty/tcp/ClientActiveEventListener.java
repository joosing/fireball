package practice.netty.tcp;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public interface ClientActiveEventListener {
    void onActive(SocketAddress remoteAddress, Channel workingChannel);
    void onInactive(SocketAddress remoteAddress);
}
