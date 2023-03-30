package practice.netty.tcp.client;

import io.netty.channel.*;
import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DefaultCustomClient implements CustomClient {
    private TcpClient client;

    public static CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = new DefaultCustomClient();
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    @Override
    public void init(EventLoopGroup eventLoopGroup) {
        // 비어있는 채널 파이프라인 생성
        List<ChannelHandler> handlers = new ArrayList<>();
        // 초기화
        init(eventLoopGroup, handlers);
    }

    /**
     * DefaultCustomClient를 상속받아 확장하는 하위 클래스에서 handlers를 구성할 수 있도록 하기 위해 존재합니다.
     * @param eventLoopGroup EventLoopGroup
     * @param handlers 채널 파이프라인에 추가할 핸들러 목록
     */
    protected void init(EventLoopGroup eventLoopGroup, List<ChannelHandler> handlers) {
        // DefaultTcpClient 생성 및 초기화
        client = new DefaultTcpClient();
        client.init(eventLoopGroup, handlers);
    }

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
