package practice.netty.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practice.netty.eventloop.ServerEventLoopGroupManager;
import practice.netty.server.TcpFileServer;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ALL")
@Slf4j
@Component
@RequiredArgsConstructor
public class FileServerRunner {
    private final ServerEventLoopGroupManager eventLoopGroupManager;
    private final ChannelSpecProvider channelSpecProvider;
    private final MessageSpecProvider messageSpecProvider;
    private static final int PORT = 12345;
    private TcpFileServer server;

    @PostConstruct
    protected void start() throws ExecutionException, InterruptedException, IOException {
        server = new TcpFileServer(messageSpecProvider, channelSpecProvider, eventLoopGroupManager.fireStore());
        server.init(eventLoopGroupManager.boss(), eventLoopGroupManager.channelIo());
        server.start(PORT).get();
    }
}
