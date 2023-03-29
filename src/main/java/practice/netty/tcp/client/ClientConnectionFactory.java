package practice.netty.tcp.client;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientConnectionFactory {
    DEFAULT(DefaultCustomClient::newConnection),
    LINE_BASED(LineBasedClient::newConnection),
    ;

    private final ConnectionSupplier newConnection;
    public ConnectionSupplier newConnection() {
        return newConnection;
    }
}
