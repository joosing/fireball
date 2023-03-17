package practice.netty.tcp;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;

public interface ReadableQueueListener {
    void onReadAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue);
    void onReadUnavailable(SocketAddress remoteAddress);
}
