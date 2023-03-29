package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.tcp.ClientFactoryType;
import practice.netty.tcp.CustomClient;
import practice.netty.tcp.TcpServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static practice.netty.tcp.LineBasedTcpServer.newServer;

@RequiredArgsConstructor
public class TcpLoopBackTestHelper {
    // 서버 포트
    private final int serverPort;
    // 클라이언트 수
    private final int nClient;
    // 클라이언트 팩토리 타입
    private final ClientFactoryType clientFactoryType;
    // 서버
    protected TcpServer server;
    // 클라이언트 목록
    protected List<CustomClient> clients;
    // 이벤트 루프 그룹
    private EventLoopGroup clientEventLoopGroup;

    // 비동기 테스트 프레임워크 설정
    private static void setUpAwaitility() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격
    }

    @BeforeEach
    protected void setUp() throws ExecutionException, InterruptedException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        // 비동기 테스트 프레임워크 설정
        setUpAwaitility();

        // 서버 생성
        server = newServer(serverPort);

        // N개 클라이언트 연결 생성
        clients = new ArrayList<>();
        clientEventLoopGroup = new NioEventLoopGroup();
        Method factoryMethod = getFactoryMethod(clientFactoryType);
        for (int i = 0; i < nClient; i++) {
            CustomClient client = (CustomClient) factoryMethod.invoke(null, "localhost", serverPort, clientEventLoopGroup);
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
        server.shutdownGracefully().get();
        clientEventLoopGroup.shutdownGracefully().sync();
    }

    private static Method getFactoryMethod(ClientFactoryType clientFactoryType) throws NoSuchMethodException {
        Class<? extends CustomClient> clientClass = clientFactoryType.getClientClass();
        String factoryMethodName = clientFactoryType.getFactoryMethodName();
        return clientClass.getDeclaredMethod(factoryMethodName, String.class, int.class, EventLoopGroup.class);
    }
}
