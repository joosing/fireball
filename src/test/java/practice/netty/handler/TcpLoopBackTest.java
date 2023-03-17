package practice.netty.handler;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.TcpClient;
import practice.netty.tcp.TcpServer;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class TcpLoopBackTest {
    protected TcpServer server;
    protected TcpClient client;

    @BeforeEach
    protected void beforeEach() throws Exception {
        // 비동기 테스트 프레임워크 설정
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격

        // 서버 및 클라이언트 연결 설정
        server = new TcpServer();
        client = new TcpClient();
        server.start(12345).get();
        client.connect("localhost", 12345).get();

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> server.isActive(client.localAddress()));
    }

    @AfterEach
    protected void afterEach() {
        client.destroy();
        server.destroy();
    }
}
