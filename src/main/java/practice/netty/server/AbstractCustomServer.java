package practice.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import org.springframework.lang.Nullable;
import practice.netty.common.HandlerFactory;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCustomServer implements CustomServer {
    private TcpServer server;

    @Override
    public void init(EventLoopGroup bossGroup, EventLoopGroup workGroup) {
        // 채널 파이프라인 생성
        List<HandlerFactory> pipelineFactory = new ArrayList<>();
        configChildHandlers(pipelineFactory);

        // Tcp 서버 생성 및 초기화
        server = new DefaultTcpServer();
        server.init(bossGroup, workGroup, pipelineFactory);
    }

    protected abstract void configChildHandlers(List<HandlerFactory> pipelineFactory);

    @Override
    public Future<Void> start(int bindPort) throws InterruptedException {
        return server.start(bindPort);
    }

    @Override
    public Future<Void> sendAll(Object message) {
        return server.sendAll(message);
    }

    @Nullable
    @Override
    public Object read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException {
        return server.read(clientAddress, timeout, unit);
    }

    @Override
    public Object readSync(SocketAddress clientAddress) throws InterruptedException {
        return server.readSync(clientAddress);
    }

    @Override
    public boolean isActive(SocketAddress clientAddress) {
        return server.isActive(clientAddress);
    }

    @Override
    public ChannelPipeline pipeline(SocketAddress remoteAddress) {
        return server.pipeline(remoteAddress);
    }

    @Override
    public Channel channel(SocketAddress remoteAddress) {
        return server.channel(remoteAddress);
    }

    @Override
    public EventLoop eventLoop(SocketAddress remoteAddress) {
        return server.eventLoop(remoteAddress);
    }

    @Override
    public Thread eventLoopThread(SocketAddress remoteAddress) throws ExecutionException, InterruptedException {
        return server.eventLoopThread(remoteAddress);
    }
}
