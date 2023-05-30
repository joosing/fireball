package io.fireball.handler.inbound;

import java.net.SocketAddress;

@FunctionalInterface
public interface ReadDataListener {
    void onReadAvailable(SocketAddress remoteAddress, Object data) throws InterruptedException;
}
