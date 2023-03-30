package practice.netty.tcp.server;

import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ExecutionException;

public final class CustomServerFactory {
    public static CustomServer newServer(CustomServerType type, int port, EventLoopGroup bossGroup,
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

    private CustomServerFactory() {}
}
