package practice.netty.connection;

import org.junit.jupiter.api.Test;
import practice.netty.helper.TcpLoopBackSingleConnectionTest;

import java.util.Objects;

import static org.awaitility.Awaitility.await;

public class ConnectionTest extends TcpLoopBackSingleConnectionTest {

    @Test
    void normalConnection() throws Exception {
        // When : 서버의 메시지 전송
        server.sendAll("status").get();
        // Then : 클라이언트 메시지 수신
        await().until(() -> Objects.equals(client.read(), "status"));
    }
}
