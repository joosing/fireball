package practice.netty.helper;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.FileServiceMessageSpecProvider;
import practice.netty.tcp.client.TcpFileClient;
import practice.netty.tcp.server.TcpFileServer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;

public class FileServiceTestHelper {
    // 이벤트 루프 그룹
    private EventLoopGroup clientWorkGroup;
    private EventLoopGroup clientFileIoGroup; // 파일 I/O 블락킹 동작을 위한 전용 이벤트 루프 그룹
    private EventLoopGroup serverWorkGroup;
    private EventLoopGroup serverAcceptGroup;
    // 클라이언트
    protected TcpFileClient client;
    // 서버
    protected TcpFileServer server;

    @BeforeEach
    protected void setUp() throws ExecutionException, InterruptedException, IOException {
        // 메시지
        FileServiceMessageSpecProvider messageSpec = new FileServiceMessageSpecProvider();
        FileServiceChannelSpecProvider channelSpecProvider = new FileServiceChannelSpecProvider();

        // 서버 시작
        serverWorkGroup = new NioEventLoopGroup();
        serverAcceptGroup = new NioEventLoopGroup();
        server = new TcpFileServer("./", messageSpec, channelSpecProvider);
        server.init(serverAcceptGroup, serverWorkGroup);
        server.start(12345).get();

        // 클라이언트 연결
        clientWorkGroup = new NioEventLoopGroup();
        clientFileIoGroup = new DefaultEventLoopGroup();
        client = new TcpFileClient(clientFileIoGroup, messageSpec, channelSpecProvider);
        client.init(clientWorkGroup);
        client.connect("localhost", 12345).get();

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().until(() -> server.isActive(client.localAddress()));
    }

    @AfterEach
    protected void tearDown() throws InterruptedException, IOException {
        clientWorkGroup.shutdownGracefully().sync();
        clientFileIoGroup.shutdownGracefully().sync();
        serverWorkGroup.shutdownGracefully().sync();
        serverAcceptGroup.shutdownGracefully().sync();
    }

}
