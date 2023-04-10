package practice.netty.tcp.server;

import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ExecutionException;

public interface CustomServer extends TcpServerRequest, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup);

    static CustomServer of(CustomServerType type, int port, EventLoopGroup bossGroup,
                                  EventLoopGroup childGroup) throws ExecutionException, InterruptedException {
        CustomServer server = newServer(type);
        server.init(bossGroup, childGroup);
        server.start(port).get();
        return server;
    }

    private static CustomServer newServer(CustomServerType type) {
        return switch (type) {
            case LINE_BASED -> new LineBasedTcpServer();
            case DEFAULT -> new DefaultCustomServer();
        };
    }
}
