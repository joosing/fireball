package practice.netty.tcp.server;

import io.netty.channel.*;
import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractCustomServer implements CustomServer {
    private TcpServer server;

    @Override
    public void init(EventLoopGroup bossGroup, EventLoopGroup workGroup) {
        // 채널 파이프라인 생성
        List<Supplier<ChannelHandler>> suppliers = configChildHandlers();

        // Tcp 서버 생성 및 초기화
        server = new DefaultTcpServer();
        server.init(bossGroup, workGroup, suppliers);
    }

    protected abstract List<Supplier<ChannelHandler>> configChildHandlers();

    @Override
    public Future<Boolean> start(int bindPort) {
        return server.start(bindPort);
    }

    @Override
    public Future<Boolean> sendAll(Object message) {
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
