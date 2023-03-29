package practice.netty.tcp;

import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.lang.Nullable;
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class LineBasedClient implements CustomClient, ReadableQueueListener {
    private TcpClient client;
    private ChannelAccessor channelTestable;
    @Nullable private volatile BlockingQueue<String> recvQueue;

    public static CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = new LineBasedClient();
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    @Override
    public void init(EventLoopGroup eventLoopGroup) {
        // Custom 채널 파이프라인 구성
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        // Inbound
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new ReadDataUpdater(LineBasedClient.this))
                        // Outbound
                        .addLast(new StringEncoder())
                        .addLast(new LineAppender("\n"));
            }
        };

        // TcpClient 생성 및 초기화
        client = new DefaultTcpClient();
        client.init(eventLoopGroup, channelInitializer);

        // 테스트 용도
        channelTestable = client;
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
    @Nullable
    public String read() throws InterruptedException {
        // 주의: recvQueue가 null 이후에 비동기적으로 채널이 닫히고 recvQueue가 null이 될 수 있기 때문에 내부
        //      변수에 참조를 저장한 후 처리합니다.
        BlockingQueue<String> tmpRecvQueue = recvQueue;
        if (tmpRecvQueue == null) {
            return null;
        }
        return tmpRecvQueue.take();
    }

    @Override
    @Nullable
    public String read(int timeout, TimeUnit unit) throws InterruptedException {
        // 주의: recvQueue가 null 이후에 비동기적으로 채널이 닫히고 recvQueue가 null이 될 수 있기 때문에 내부
        //      변수에 참조를 저장한 후 처리합니다.
        BlockingQueue<String> tmpRecvQueue = recvQueue;
        if (tmpRecvQueue == null) {
            return null;
        }
        return tmpRecvQueue.poll(timeout, unit);
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
    public void onReadAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue) {
        this.recvQueue = recvQueue;
    }

    @Override
    public void onReadUnavailable(SocketAddress remoteAddress) {
        recvQueue = null;
    }

    @Override
    public ChannelPipeline pipeline() {
        return channelTestable.channel().pipeline();
    }

    @Override
    public Channel channel() {
        return channelTestable.channel();
    }

    @Override
    public EventLoop eventLoop() {
        return channelTestable.channel().eventLoop();
    }

    @Override
    public Thread eventLoopThread() throws ExecutionException, InterruptedException {
        AtomicReference<Thread> thread = new AtomicReference<>();
        CompletableFuture<Void> getThreadFuture = new CompletableFuture<>();
        channelTestable.channel().writeAndFlush("").addListener((ChannelFutureListener) future -> {
            thread.set(Thread.currentThread());
            getThreadFuture.complete(null);
        });
        getThreadFuture.get();
        return thread.get();
    }
}
