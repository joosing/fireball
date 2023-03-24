package practice.netty.handler;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.LineBasedTcpClient;
import practice.netty.tcp.LineBasedTcpServer;
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

        // 서버 생성 및 시작
        server = new LineBasedTcpServer();
        server.init();
        server.start(12345).get();

        // 클라이언트 생성 및 연결
        client = new LineBasedTcpClient();
        client.init();
        client.connect("localhost", 12345).get();

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> server.isActive(client.localAddress()));
    }

    @AfterEach
    protected void afterEach() throws Exception {
        client.destroy().get();
        server.destroy().get();
    }
}
