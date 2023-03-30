package practice.netty.tcp.client;

import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ExecutionException;

public final class CustomClientFactor {

    public static CustomClient newConnection(CustomClientType type, String ip, int port,
                                             EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = newClient(type);
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    private static CustomClient newClient(CustomClientType type) {
        return switch (type) {
            case LINE_BASED -> new LineBasedClient();
            case DEFAULT -> new DefaultCustomClient();
        };
    }

    private CustomClientFactor() {}
}
