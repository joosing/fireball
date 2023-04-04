package practice.netty.helper;

import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;

public class TcpLoopbackSingleClientHelper extends TcpLoopBackTestHelper {
    protected CustomClient client;

    public TcpLoopbackSingleClientHelper(TcpLoopbackTestSetting setting) {
        super(setting);
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        client = clients.get(0);
    }
}
