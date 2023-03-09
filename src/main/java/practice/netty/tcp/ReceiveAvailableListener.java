package practice.netty.tcp;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;

public interface ReceiveAvailableListener {
    void onReceiveAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue);
    void onReceiveUnavailable(SocketAddress remoteAddress);
}
