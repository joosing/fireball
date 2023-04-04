package practice.netty.connection;

import org.junit.jupiter.api.Test;
import practice.netty.helper.TcpLoopbackSingleClientHelper;
import practice.netty.helper.TcpLoopbackTestSetting;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

import java.util.Objects;

import static org.awaitility.Awaitility.await;

public class ConnectionTest extends TcpLoopbackSingleClientHelper {

    public ConnectionTest() {
        super(TcpLoopbackTestSetting.builder()
                .serverPort(12345)
                .nClient(1)
                .serverType(CustomServerType.LINE_BASED)
                .clientType(CustomClientType.LINE_BASED)
                .build());
    }

    @Test
    void normalConnection() throws Exception {
        // When : 서버의 메시지 전송
        server.sendAll("status").get();
        // Then : 클라이언트 메시지 수신
        await().until(() -> Objects.equals(client.readSync(), "status"));
    }
}
