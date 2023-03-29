package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.awaitility.Awaitility;
import practice.netty.tcp.CustomClient;
import practice.netty.tcp.TcpServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static practice.netty.tcp.LineBasedClient.newConnection;
import static practice.netty.tcp.LineBasedTcpServer.newServer;

public class TcpLoopBackTestHelper {
    // 서버
    private TcpServer server;

    // 클라이언트
    private EventLoopGroup clientEventLoopGroup;
    private List<CustomClient> clients;

    // 비동기 테스트 프레임워크 설정
    private static void setUpAwaitility() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격
    }

    public void setUp(int serverPort, int nClient) throws ExecutionException, InterruptedException {
        // 비동기 테스트 프레임워크 설정
        setUpAwaitility();

        // 서버 생성
        server = newServer(serverPort);

        // N개 클라이언트 연결 생성
        clients = new ArrayList<>();
        clientEventLoopGroup = new NioEventLoopGroup();
        for (int i = 0; i < nClient; i++) {
            CustomClient client = newConnection("localhost", serverPort, clientEventLoopGroup);
            clients.add(client);
        }

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        for (var client : clients) {
            await().atMost(1000, TimeUnit.MILLISECONDS)
                    .until(() -> server.isActive(client.localAddress()));
        }
    }

    public void shutdown() throws ExecutionException, InterruptedException {
        server.shutdownGracefully().get();
        clientEventLoopGroup.shutdownGracefully().sync();
    }

    public TcpServer server() {
        return server;
    }

    public CustomClient client(int index) {
        if (index < 0 || index >= clients.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        return clients.get(index);
    }
}
