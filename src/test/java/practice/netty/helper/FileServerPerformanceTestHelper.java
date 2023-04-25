package practice.netty.helper;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.experimental.Accessors;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.FileServiceMessageSpecProvider;
import practice.netty.tcp.client.TcpFileClient;
import practice.netty.tcp.server.TcpFileServer;
import practice.netty.util.FutureUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;

@Accessors(fluent = true)
public class FileServerPerformanceTestHelper {
    // 클라이언트
    @Getter
    private List<TcpFileClient> clients;
    private NioEventLoopGroup workGroup;
    private NioEventLoopGroup fileIoGroup;
    // 서버
    protected TcpFileServer server;
    private EventLoopGroup serverWorkGroup;
    private EventLoopGroup serverAcceptGroup;

    public void setUp(int nClient) throws ExecutionException, InterruptedException, IOException {
        // 메시지 및 채널 스펙
        FileServiceChannelSpecProvider channelSpecProvider = new FileServiceChannelSpecProvider();
        FileServiceMessageSpecProvider messageSpec = new FileServiceMessageSpecProvider(channelSpecProvider);

        // 서버 시작
        serverWorkGroup = new NioEventLoopGroup();
        serverAcceptGroup = new NioEventLoopGroup();
        server = new TcpFileServer(messageSpec, channelSpecProvider);
        server.init(serverAcceptGroup, serverWorkGroup);
        server.start(12345).get();

        // 다중 클라이언트 연결
        workGroup = new NioEventLoopGroup();
        fileIoGroup = new NioEventLoopGroup();
        clients = new CopyOnWriteArrayList<>();
        IntStream.range(0, nClient).parallel()
                .forEach(i -> {
                    var client = new TcpFileClient(fileIoGroup, messageSpec, channelSpecProvider);
                    client.init(workGroup);
                    var future = client.connect("localhost", 12345);
                    FutureUtils.get(future);
                    clients.add(client);
                });

        // 서버가 각 클라이언트와 통신 가능한 상태가 될 때까지 대기
        clients.stream().parallel().forEach(client -> {
            await().until(() -> server.isActive(client.localAddress()));
        });
    }

    public void tearDown() throws InterruptedException, IOException {
        workGroup.shutdownGracefully().sync();
        fileIoGroup.shutdownGracefully().sync();
        serverWorkGroup.shutdownGracefully().sync();
        serverAcceptGroup.shutdownGracefully().sync();
    }
}
