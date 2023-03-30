package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientFactor;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServer;
import practice.netty.tcp.server.CustomServerFactory;
import practice.netty.tcp.server.CustomServerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@RequiredArgsConstructor
public class TcpLoopBackTestHelper {
    // 서버 포트
    private final int serverPort;
    // 클라이언트 수
    private final int nClient;
    // 클라이언트 팩토리 타입
    private final CustomServerType serverType;
    // 클라이언트 팩토리 타입
    private final CustomClientType clientType;
    // 서버
    protected CustomServer server;
    // 클라이언트 목록
    protected List<CustomClient> clients;
    // 이벤트 루프 그룹
    EventLoopGroup clientEventLoopGroup;
    EventLoopGroup serverBossEventLoopGroup;
    EventLoopGroup serverChildEventLoopGroup;

    // 비동기 테스트 프레임워크 설정
    private static void setUpAwaitility() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격
    }

    @BeforeEach
    protected void setUp() throws Exception {
        // 비동기 테스트 프레임워크 설정
        setUpAwaitility();

        // 서버 생성
        serverBossEventLoopGroup = new NioEventLoopGroup();
        serverChildEventLoopGroup = new NioEventLoopGroup();
        server = CustomServerFactory.newServer(serverType, 12345, serverBossEventLoopGroup, serverChildEventLoopGroup);

        // 클라이언트 이벤트 루프 그룹
        clientEventLoopGroup = new NioEventLoopGroup();

        // N개 클라이언트 연결 생성
        clients = new ArrayList<>();
        for (int i = 0; i < nClient; i++) {
            CustomClient client = CustomClientFactor.newConnection(clientType, "localhost", serverPort, clientEventLoopGroup);
            clients.add(client);
        }

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        for (var client : clients) {
            await().atMost(1000, TimeUnit.MILLISECONDS)
                    .until(() -> server.isActive(client.localAddress()));
        }
    }

    @AfterEach
    public void shutdown() throws ExecutionException, InterruptedException {
        serverBossEventLoopGroup.shutdownGracefully().sync();
        serverChildEventLoopGroup.shutdownGracefully().sync();
        clientEventLoopGroup.shutdownGracefully().sync();
    }
}
