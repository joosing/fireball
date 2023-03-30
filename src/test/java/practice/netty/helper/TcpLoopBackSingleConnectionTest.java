package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.TcpServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static practice.netty.tcp.client.CustomClientFactor.newConnection;
import static practice.netty.tcp.server.LineBasedTcpServer.newServer;

public class TcpLoopBackSingleConnectionTest {
    // 서버
    protected TcpServer server;

    // 클라이언트
    private EventLoopGroup clientEventLoopGroup;
    protected CustomClient client;

    @BeforeEach
    void setUp () throws ExecutionException, InterruptedException {
        // 비동기 테스트 프레임워크 설정
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격

        // 서버 및 클라이언트 생성 후 연결
        server = newServer(12345);
        clientEventLoopGroup = new NioEventLoopGroup();
        client = newConnection(CustomClientType.LINE_BASED,"localhost", 12345, clientEventLoopGroup);

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> server.isActive(client.localAddress()));
    }

    @AfterEach
    void tearDown() throws ExecutionException, InterruptedException {
        server.shutdownGracefully().get();
        clientEventLoopGroup.shutdownGracefully().sync();
    }
}
