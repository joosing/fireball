package practice.netty.handler;

import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import practice.netty.handler.outbound.OutboundBlockingHandler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
public class BlockingHandlerTest extends TcpLoopBackTest {

    @Test
    void normal() throws Exception {
        // When : 메시지 전송
        server.sendAll("status");

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
        server.sendAll("status");

        // Then : 지연 이후 메시지 수신
        await().atLeast(blockingMillis, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }

    @Test
    void blockingHandlerWithOwnEventLoop() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널을 전용 이벤트 루프와 함께 생성
        final long blockingMillis = 3000;
        client.test().pipeline().addLast(new DefaultEventLoopGroup(),
                new OutboundBlockingHandler(blockingMillis));
        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.sendAll("status");

        // Then : 즉시 메시지 수신
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }
}
