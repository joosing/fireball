package practice.netty.helper;

import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

public class TcpLoopbackDoubleClientHelper extends TcpLoopBackTestHelper {
    protected CustomClient clientOne;
    protected CustomClient clientTwo;

    public TcpLoopbackDoubleClientHelper(int serverPort, CustomServerType serverType, CustomClientType clientType) {
        super(serverPort, 2, serverType, clientType);
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        clientOne = clients.get(0);
        clientTwo = clients.get(1);
    }
}
