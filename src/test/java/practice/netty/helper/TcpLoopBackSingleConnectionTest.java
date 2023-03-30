package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServer;
import practice.netty.tcp.server.CustomServerFactory;
import practice.netty.tcp.server.CustomServerType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static practice.netty.tcp.client.CustomClientFactor.newConnection;

public class TcpLoopBackSingleConnectionTest {
    // 서버
    private EventLoopGroup serverBossEventLoopGroup;
    private EventLoopGroup serverChildEventLoopGroup;
    protected CustomServer server;

    // 클라이언트
    private EventLoopGroup clientEventLoopGroup;
    protected CustomClient client;

    @BeforeEach
    void setUp () throws ExecutionException, InterruptedException {
        // 비동기 테스트 프레임워크 설정
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격

        // 서버 생성 및 바인딩
        serverBossEventLoopGroup = new NioEventLoopGroup();
        serverChildEventLoopGroup = new NioEventLoopGroup();
        server = CustomServerFactory.newServer(CustomServerType.LINE_BASED, 12345, serverBossEventLoopGroup, serverChildEventLoopGroup);

        // 클라이언트 생성 및 연결
        clientEventLoopGroup = new NioEventLoopGroup();
        client = newConnection(CustomClientType.LINE_BASED,"localhost", 12345, clientEventLoopGroup);

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> server.isActive(client.localAddress()));
    }

    @AfterEach
    void tearDown() throws ExecutionException, InterruptedException {
        serverBossEventLoopGroup.shutdownGracefully().sync();
        serverChildEventLoopGroup.shutdownGracefully().sync();
        clientEventLoopGroup.shutdownGracefully().sync();
    }
}
