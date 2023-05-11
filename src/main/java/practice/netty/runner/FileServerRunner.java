package practice.netty.runner;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practice.netty.specification.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;
import practice.netty.tcp.server.TcpFileServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class FileServerRunner {
    private static final int PORT = 12345;
    // 이벤트 루프 그룹
    private EventLoopGroup serverWorkGroup;
    private EventLoopGroup serverAcceptGroup;
    private EventLoopGroup fileStoreGroup;
    // 서버
    @Getter private TcpFileServer server;

    @PostConstruct
    protected void start() throws ExecutionException, InterruptedException, IOException {
        // 스펙
        ChannelSpecProvider channelSpecProvider = new ChannelSpecProvider();
        MessageSpecProvider messageSpec = new MessageSpecProvider(channelSpecProvider);

        // 서버 시작
        serverWorkGroup = new NioEventLoopGroup();
        serverAcceptGroup = new NioEventLoopGroup();
        fileStoreGroup = new NioEventLoopGroup();
        server = new TcpFileServer(messageSpec, channelSpecProvider, fileStoreGroup);
        server.init(serverAcceptGroup, serverWorkGroup);
        server.start(PORT).get();

        log.info("File server started on port {}", PORT);
    }

    @PreDestroy
    protected void shutdown() throws InterruptedException, IOException {
        serverWorkGroup.shutdownGracefully().sync();
        serverAcceptGroup.shutdownGracefully().sync();
        fileStoreGroup.shutdownGracefully().sync();
    }
}
