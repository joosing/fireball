package practice.netty.tcp.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import org.springframework.lang.Nullable;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This abstract class help for you to implemente CustomClilent.
 * This makes all TcpClient methods available and forces you to construct a specialized channel pipeline.
 */
public abstract class AbstractCustomClient implements CustomClient {
    private TcpClient client;

    @Override
    public void init(EventLoopGroup eventLoopGroup) {
        // 채널 파이프라인 생성
        List<HandlerWorkerPair> handlers = new ArrayList<>();
        configHandlers(handlers);

        // TcpClient 생성 및 초기화
        client = new DefaultTcpClient();
        client.init(eventLoopGroup, handlers);
    }

    protected abstract void configHandlers(List<HandlerWorkerPair> handlers);

    @Override
    public Future<Boolean> connect(String ip, int port) {
        return client.connect(ip, port);
    }

    @Override
    public Future<Boolean> disconnect() {
        return client.disconnect();
    }

    @Override
    public Future<Boolean> send(Object data) {
        return client.send(data);
    }

    @Override
    public Object readSync() throws InterruptedException {
        return client.readSync();
    }

    @Override
    @Nullable
    public Object read(int timeout, TimeUnit unit) throws InterruptedException {
        return client.read(timeout, unit);
    }

    @Override
    public SocketAddress localAddress() {
        return client.localAddress();
    }

    @Override
    public boolean isActive() {
        return client.isActive();
    }

    @Override
    public ChannelPipeline pipeline() {
        return client.pipeline();
    }

    @Override
    public Channel channel() {
        return client.channel();
    }

    @Override
    public EventLoop eventLoop() {
        return client.eventLoop();
    }

    @Override
    public Thread eventLoopThread() throws ExecutionException, InterruptedException {
        return client.eventLoopThread();
    }
}
