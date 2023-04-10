package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.server.CustomServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class TcpLoopBackTestHelper {
    // 설정
    private final TcpLoopbackTestSetting setting;
    // 서버
    protected CustomServer server;
    private EventLoopGroup serverBossEventLoopGroup;
    private EventLoopGroup serverChildEventLoopGroup;
    // 클라이언트
    protected List<CustomClient> clients;
    private EventLoopGroup clientEventLoopGroup;

    public TcpLoopBackTestHelper(TcpLoopbackTestSetting setting) {
        this.setting = setting;
    }

    @BeforeEach
    protected void setUp() throws Exception {
        // 비동기 테스트 프레임워크 설정
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격

        // 서버 생성
        serverBossEventLoopGroup = new NioEventLoopGroup();
        serverChildEventLoopGroup = new NioEventLoopGroup();
        server = CustomServer.of(setting.getServerType(), setting.getServerPort(), serverBossEventLoopGroup, serverChildEventLoopGroup);

        // N개 클라이언트 연결 생성
        clientEventLoopGroup = new NioEventLoopGroup();
        clients = new ArrayList<>();
        for (int i = 0; i < setting.getNClient(); i++) {
            CustomClient client = CustomClient.of(setting.getClientType(), "localhost", setting.getServerPort(), clientEventLoopGroup);
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
