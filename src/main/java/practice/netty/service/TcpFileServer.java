package practice.netty.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import practice.netty.eventloop.ServerEventLoopGroupManager;
import practice.netty.pipeline.PipelineFactory;

import java.util.concurrent.ExecutionException;

@Component
public class TcpFileServer implements FilerServer {
    private final ServerEventLoopGroupManager eventLoopGroupManager;
    private final PipelineFactory pipelineFactory;
    private final TcpServer server;

    @Autowired
    public TcpFileServer(ServerEventLoopGroupManager eventLoopGroupManager,
                         @Qualifier("tcpFileServerPipelineFactory") PipelineFactory pipelineFactory) {
        this.eventLoopGroupManager = eventLoopGroupManager;
        this.pipelineFactory = pipelineFactory;
        this.server = new DefaultTcpServer();
    }

    @Override
    public void start(int bindPort) throws InterruptedException, ExecutionException {
        server.init(eventLoopGroupManager.boss(),
                eventLoopGroupManager.channelIo(),
                pipelineFactory.get());

        server.start(bindPort).get();
    }
}
