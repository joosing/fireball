package practice.netty.helper;

import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

public class TcpLoopbackSingleClientHelper extends TcpLoopBackTestHelper {
    protected CustomClient client;

    public TcpLoopbackSingleClientHelper(int serverPort, CustomServerType serverType, CustomClientType clientType) {
        super(serverPort, 1, serverType, clientType);
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        client = clients.get(0);
    }
}
