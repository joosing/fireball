package practice.netty.tcp.common;

import java.net.SocketAddress;

@FunctionalInterface
public interface ReadDataListener {
    void onReadAvailable(SocketAddress remoteAddress, Object data) throws InterruptedException;
}
