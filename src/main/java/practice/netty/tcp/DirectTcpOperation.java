package practice.netty.tcp;

import java.net.SocketAddress;
import java.util.concurrent.Future;

public interface DirectTcpOperation {
    Future<Boolean> connect(String ip, int port);

    Future<Boolean> disconnect();

    Future<Boolean> send(Object data);

    SocketAddress localAddress();

    boolean isActive();
}
