package practice.netty.runner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practice.netty.configuration.ServerEventLoopGroupConfig;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;
import practice.netty.tcp.server.TcpFileServer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileServerRunner {
    private final ServerEventLoopGroupConfig eventLoopGroupConfig;
    private static final int PORT = 12345;
    // 서버
    @Getter private TcpFileServer server;

    @PostConstruct
    protected void start() throws ExecutionException, InterruptedException, IOException {
        // 스펙
        ChannelSpecProvider channelSpecProvider = new ChannelSpecProvider();
        MessageSpecProvider messageSpec = new MessageSpecProvider(channelSpecProvider);

        // 이벤트루프 그룹
        var bossGroup = eventLoopGroupConfig.bossEventLoopGroup();
        var channelIoGroup = eventLoopGroupConfig.ioEventLoopGroup();
        var fileStoreGroup = eventLoopGroupConfig.fileStoreEventLoopGroup();
        // 서버 시작
        server = new TcpFileServer(messageSpec, channelSpecProvider, fileStoreGroup);
        server.init(bossGroup, channelIoGroup);
        server.start(PORT).get();

        log.info("File server started on port {}", PORT);
    }
}
