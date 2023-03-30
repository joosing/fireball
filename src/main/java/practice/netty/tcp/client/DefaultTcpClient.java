package practice.netty.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.lang.Nullable;
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.util.ChannelAccessUtil;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.*;

import static practice.netty.tcp.util.PropagateChannelFuture.propagateChannelFuture;

public class DefaultTcpClient implements TcpClient {
    private Bootstrap bootstrap;
    private volatile Channel channel;
    private volatile BlockingQueue<Object> readQueue;

    @Override
    public void init(EventLoopGroup eventLoopGroup, List<ChannelHandler> handlers) {
        // 읽기 큐
        readQueue = new LinkedBlockingQueue<>();

        // 자신에게 수신 메시지를 전달해 줄 핸들러 추가
        handlers.add(new ReadDataUpdater(this));

        // 부트스트랩
        bootstrap = new Bootstrap();

        // 연결 설정
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(handlers.toArray(ChannelHandler[]::new));
                    }
                });
    }

    @Override
    public Future<Boolean> connect(String ip, int port) {
        // 타겟 주소 설정
        bootstrap.remoteAddress(ip, port);

        /* 주의! bootstrap.connect()가 반환하는 ChannelFuture 객체를 그대로 사용자에게 반환하면 프로그램이 불안정한 상태에 놓일 수 있습니다.
         * bootstrap.connect()가 반환하는 ChannelFuture 객체는 등록된 리스너가 호출되기 이전에 완료 상태가 되는데 그렇게 되면 channel이
         * 초기화 되지 않은 상태에 TcpClient 구현체를 사용자가 사용하게 될 수 있습니다. 사용자가 연결 완료를 인지한 시점 부터 즉시 통신할 수
         * 있는 안정적인 상태를 제공해야 합니다.
         */
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        // 연결
        bootstrap.connect().addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
            }
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    @Override
    public Future<Boolean> disconnect() {
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        channel.close().addListener((ChannelFutureListener) channelFuture -> {
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    @Override
    public Future<Boolean> send(Object data) {
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        channel.writeAndFlush(data).addListener((ChannelFutureListener) channelFuture -> {
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    /**
     * 수신된 메시지를 읽습니다. 읽을 메시지가 없는 경우 수신될 때 까지 무한히 대기합니다.
     */
    @Override
    public Object readSync() throws InterruptedException {
        return readQueue.take();
    }

    /**
     * 수신된 메시지를 읽습니다. 읽을 메시지가 없는 경우 수신될 때 까지 timeout 시간 만큼 대기합니다.
     */
    @Override
    @Nullable
    public Object read(int timeout, TimeUnit unit) throws InterruptedException {
        return readQueue.poll(timeout, unit);
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void onReadAvailable(SocketAddress remoteAddress, Object data) {
        readQueue.add(data);
    }

    @Override
    public ChannelPipeline pipeline() {
        return ChannelAccessUtil.pipeline(channel);
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public EventLoop eventLoop() {
        return ChannelAccessUtil.eventLoop(channel);
    }

    @Override
    public Thread eventLoopThread() throws ExecutionException, InterruptedException {
        return ChannelAccessUtil.eventLoopThread(channel);
    }
}
