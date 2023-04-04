package practice.netty.helper;

import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;

public class TcpLoopbackDoubleClientHelper extends TcpLoopBackTestHelper {
    protected CustomClient clientOne;
    protected CustomClient clientTwo;

    public TcpLoopbackDoubleClientHelper(TcpLoopbackTestSetting setting) {
        super(setting);
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        clientOne = clients.get(0);
        clientTwo = clients.get(1);
    }
}
