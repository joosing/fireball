package practice.netty.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practice.netty.tcp.TcpClient;
import practice.netty.tcp.TcpServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static practice.netty.tcp.LineBasedTcpClient.newConnection;
import static practice.netty.tcp.LineBasedTcpServer.newServer;

public class EventLoopTest {
    TcpServer server;
    TcpClient clientOne;
    TcpClient clientTwo;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        server = newServer(12345);
    }

    @AfterEach
    void tearDown() throws ExecutionException, InterruptedException {
        server.shutdownGracefully().get();
        if (clientOne != null) {
            clientOne.shutdownGracefully().get();
        }
        if (clientTwo != null) {
            clientTwo.shutdownGracefully().get();
        }
    }

    // 이벤트 루프 공유
    @Test
    void oneEventLoopHandleMultiChannels() throws Exception {
        // When: 2개의 클라이언트 연결 (1개 이벤트 루프 공유)
        int nThread = 1;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(nThread);
        clientOne = newConnection("localhost", 12345, eventLoopGroup);
        clientTwo = newConnection("localhost", 12345, eventLoopGroup);

        // Then: 하나의 이벤트 루프 공유하여 정상 통신
        assertSame(clientOne.test().eventLoop(), clientTwo.test().eventLoop());
        assertNormalCommunication();
    }

    // 각각의 이벤트 루프 할당
    @Test
    void twoEventLoopHandleEachChannel() throws Exception {
        // When: 2개의 클라이언트 연결 (각각의 이벤트 루프)
        int nThread = 2;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(nThread);
        clientOne = newConnection("localhost", 12345, eventLoopGroup);
        clientTwo = newConnection("localhost", 12345, eventLoopGroup);

        // Then: 각각의 이벤트 루프가 할당되고 정상 통신
        assertNotSame(clientOne.test().eventLoop(), clientTwo.test().eventLoop());
        assertNormalCommunication();
    }

    // 서버와 클라이언트 간 일반적인 데이터 교환 테스트
    void assertNormalCommunication() throws Exception {
        // 서버의 통신 가능 상태 대기
        await().until(() -> server.isActive(clientOne.localAddress()) &&
                            server.isActive(clientTwo.localAddress()));

        // 서버 -> 클라이언트
        server.sendAll("Hello All");
        assertEquals("Hello All", clientOne.read(1, TimeUnit.SECONDS));
        assertEquals("Hello All", clientTwo.read(1, TimeUnit.SECONDS));

        // 클라이언트 -> 서버
        clientOne.send("Hello I am One");
        assertEquals("Hello I am One", server.read(clientOne.localAddress(), 1, TimeUnit.SECONDS));
        clientTwo.send("Hello I am Two");
        assertEquals("Hello I am Two", server.read(clientTwo.localAddress(), 1, TimeUnit.SECONDS));
    }

    // 이벤트루프 그룹을 닫을 경우 하위에 연결된 모든 채널들이 닫힙니다.
    @Test
    void shutdownAll() throws Exception {
        // Given: 2개의 클라이언트 연결
        EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup(2);
        clientOne = newConnection("localhost", 12345, clientEventLoopGroup);
        clientTwo = newConnection("localhost", 12345, clientEventLoopGroup);

        // When: 이벤트 루프 그룹 닫기
        clientEventLoopGroup.shutdownGracefully().sync();

        // Then: 하위 모든 채널 연결 닫힘
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> !server.isActive(clientOne.localAddress()) &&
                             !server.isActive(clientTwo.localAddress()));
    }

    @Test
    void keepAliveEvenIfChannelGetClosed() throws Exception {
        // Given: 연결된 채널의 이벤트 루프 쓰레드 획득
        EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup(1);
        clientOne = newConnection("localhost", 12345, clientEventLoopGroup);
        Thread threadOne = clientOne.test().eventLoopThread();

        // When: 연결 끊기
        clientOne.disconnect();
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> !server.isActive(clientOne.localAddress()));

        // Then: 이벤트 루프의 쓰레드는 살아있음
        await().during(3000, TimeUnit.MILLISECONDS)
                .until(threadOne::isAlive);
    }
}
