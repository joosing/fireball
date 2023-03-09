package practice.netty.handler;

import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import practice.netty.handler.outbound.OutboundDelayHandler;
import practice.netty.tcp.TcpClient;
import practice.netty.tcp.TcpServer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BlockingHandlerTest {
    TcpServer server;
    TcpClient client;

    @BeforeEach
    void beforeEach() throws Exception {
        server = new TcpServer();
        client = new TcpClient();
        server.start(12345).get();
        client.connect("localhost", 12345).get();
    }

    @AfterEach
    void afterEach() {
        client.destroy();
        server.destroy();
    }

    @Test
    @DisplayName("핸들러에서 블락킹이 발생하면 채널의 이벤트 처리가 중단된다")
    void blockingHandler() throws Exception {
        // Given : Blocking 동작을 가진 Handler 추가
        client.test().pipeline().addLast(new OutboundDelayHandler(3000));

        // When : 클라이언트에서 메시지 1개 전송(Blocking), 서버에서 10개 메시지 전송
        client.send("Command\n");

        for (int i = 0; i < 10; i++) {
            server.send("Response(%d)\n".formatted(i));
        }

        // Then : 클라이언트 수신 실패
        for (int i = 0; i < 10; i++) {
            Optional<String> response = client.receive(100, TimeUnit.MILLISECONDS);
            Assertions.assertTrue(response.isEmpty());
        }
    }

    @Test
    @DisplayName("블락킹이 발생하는 핸들러에 전용쓰레드를 할당하면 채널의 이벤트 처리가 중단되지 않는다")
    void blockingHandlerWithDedicatedExecutor() throws Exception {
        // Given : Blocking 동작을 가진 Handler 독립적인 쓰레드로 처리
        client.test().pipeline().addLast(new DefaultEventLoopGroup(), new OutboundDelayHandler(3000));

        // When : 클라이언트에서 메시지 1개 전송(Blocking), 서버에서 10개 메시지 전송
        client.send("Command\n");

        for (int i = 0; i < 10; i++) {
            server.send("Response(%d)\n".formatted(i));
        }

        // Then : 클라이언트에서 10개 메시지 수신
        for (int i = 0; i < 10; i++) {
            Optional<String> response = client.receive(100, TimeUnit.MILLISECONDS);
            Assertions.assertTrue(response.isPresent());
            Assertions.assertEquals("Response(%d)".formatted(i), response.get());
        }
    }
}
