package practice.netty.handler;

import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practice.netty.handler.outbound.OutboundBlockingHandler;
import practice.netty.tcp.TcpClient;
import practice.netty.tcp.TcpServer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
public class BlockingHandlerTest {
    TcpServer server;
    TcpClient client;

    @BeforeEach
    void beforeEach() throws Exception {
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
    void afterEach() {
        client.destroy();
        server.destroy();
    }

    @Test
    void normal() throws Exception {
        // When : 메시지 전송
        server.send("status");

        // Then : 즉시 메시지 수신
        await().until(() -> Objects.equals(client.read(), "status"));
    }

    @Test
    void blockingHandler() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널
        final long blockingMillis = 3000;
        client.test().pipeline().addLast(new OutboundBlockingHandler(blockingMillis));
        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.send("status");

        // Then : 지연 이후 메시지 수신
        await().atLeast(blockingMillis, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }

    @Test
    void blockingHandlerWithOwnEventLoop() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널을 전용 이벤트 루프와 함께 생성
        final long blockingMillis = 3000;
        client.test().pipeline().addLast(new DefaultEventLoopGroup(), new OutboundBlockingHandler(blockingMillis));
        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.send("status");

        // Then : 즉시 메시지 수신
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }
}
