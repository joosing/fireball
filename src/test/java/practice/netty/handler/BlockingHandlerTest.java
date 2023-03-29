package practice.netty.handler;

import io.netty.channel.DefaultEventLoopGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practice.netty.handler.outbound.OutboundBlockingHandler;
import practice.netty.helper.TcpLoopBackTestHelper;
import practice.netty.tcp.ClientFactoryType;
import practice.netty.tcp.CustomClient;
import practice.netty.tcp.TcpServer;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class BlockingHandlerTest {
    TcpLoopBackTestHelper helper = new TcpLoopBackTestHelper();
    TcpServer server;
    CustomClient client;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException {
        helper.setUp(12345, 1, ClientFactoryType.LINE_BASED);
        server = helper.server();
        client = helper.client(0);
    }

    @AfterEach
    void shutdown() throws ExecutionException, InterruptedException {
        helper.shutdown();
    }

    @Test
    void normalConnection() throws Exception {
        // When : 서버의 메시지 전송
        server.sendAll("status").get();
        // Then : 클라이언트 메시지 수신
        await().until(() -> Objects.equals(client.read(), "status"));
    }

    @Test
    void blockingHandler() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널
        client.pipeline().addLast(new OutboundBlockingHandler(3000));
        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.sendAll("status").get();

        // Then : 지연 이후 메시지 수신
        await().atLeast(2900, TimeUnit.MILLISECONDS)
                .atMost(3100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }

    @Test
    void blockingHandlerWithOwnEventLoop() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널을 전용 이벤트 루프와 함께 생성
        client.pipeline().addLast(new DefaultEventLoopGroup(), new OutboundBlockingHandler(3000));

        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.sendAll("status").get();

        // Then : 즉시 메시지 수신
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.read(), "status"));
    }
}
