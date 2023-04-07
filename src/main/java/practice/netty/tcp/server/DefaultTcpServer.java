package practice.netty.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.lang.Nullable;
import practice.netty.handler.inbound.ClientActiveNotifier;
import practice.netty.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.common.Handler;
import practice.netty.util.ChannelAccessUtils;

import java.net.SocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static practice.netty.util.PropagateChannelFuture.propagateChannelFuture;

public class DefaultTcpServer implements TcpServer {
    private ServerBootstrap bootstrap;
    private ConcurrentHashMap<SocketAddress, ActiveChannel> activeChannels;

    @Override
    public void init(EventLoopGroup bossGroup, EventLoopGroup childGroup, List<Handler> childHandlers) {

        // 접속 알림 핸들러
        childHandlers.add(Handler.of(new ClientActiveNotifier(this)));
        // 데이터 수신 알림 핸들러
        childHandlers.add(Handler.of(new ReadDataUpdater(this)));

        bootstrap = new ServerBootstrap();
        activeChannels = new ConcurrentHashMap<>();
        bootstrap.group(bossGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        childHandlers.forEach(config -> {
                            ch.pipeline().addLast(config.workGroup(), config.handler());
                        });
                    }
                });
    }

    @Override
    public Future<Boolean> start(int bindPort) {
        bootstrap.localAddress("0.0.0.0", bindPort);
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        bootstrap.bind().addListener((ChannelFutureListener) channelFuture -> {
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    @Override
    public Future<Boolean> sendAll(Object message) {
        List<CompletableFuture<Boolean>> sendFutures = new ArrayList<>();
        activeChannels.values().forEach(activeChannel -> {
            var sendFuture = new CompletableFuture<Boolean>();
            activeChannel.channel().writeAndFlush(message).addListener((ChannelFutureListener) channelFuture -> {
                propagateChannelFuture(sendFuture, channelFuture);
            });
            sendFutures.add(sendFuture);
        });

        return CompletableFuture
                .allOf(sendFutures.toArray(CompletableFuture[]::new))
                .thenApply(ignored ->
                        sendFutures.stream()
                                .map(CompletableFuture::join)
                                .reduce(Boolean::logicalAnd)
                                .orElse(false));
    }

    @Override
    @Nullable
    public Object read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException {
        BlockingQueue<Object> readQueue = activeChannels.get(clientAddress).readQueue();
        if (readQueue == null) {
            // 연결이 안된 상태이다. 예외를 던진다.
            throw new NotYetConnectedException();
        }
        return readQueue.poll(timeout, unit);
    }

    @Override
    public Object readSync(SocketAddress clientAddress) throws InterruptedException {
        BlockingQueue<Object> readQueue = activeChannels.get(clientAddress).readQueue();
        if (readQueue == null) {
            // 연결이 안된 상태이다. 예외를 던진다.
            throw new NotYetConnectedException();
        }
        return readQueue.take();
    }

    @Override
    public boolean isActive(SocketAddress clientAddress) {
        return activeChannels.containsKey(clientAddress);
    }

    @Override
    public void onActive(SocketAddress remoteAddress, Channel workingChannel) {
        ActiveChannel activeChannel = new ActiveChannel(remoteAddress, workingChannel, new LinkedBlockingQueue<>());
        activeChannels.put(remoteAddress, activeChannel);
    }

    @Override
    public void onInactive(SocketAddress remoteAddress) {
        activeChannels.remove(remoteAddress);
    }

    @Override
    public void onReadAvailable(SocketAddress remoteAddress, Object data) {
        BlockingQueue<Object> queue = activeChannels.get(remoteAddress).readQueue();
        if (queue != null) {
            // 연결이 끊어진 후 수신되는 데이터는 처리할 수 없다.
            queue.add(data);
        }
    }

    @Override
    public ChannelPipeline pipeline(SocketAddress remoteAddress) {
        ActiveChannel activeChannel = getActiveChannel(remoteAddress);
        return ChannelAccessUtils.pipeline(activeChannel.channel());
    }

    @Override
    public Channel channel(SocketAddress remoteAddress) {
        ActiveChannel activeChannel = getActiveChannel(remoteAddress);
        return activeChannel.channel();
    }

    @Override
    public EventLoop eventLoop(SocketAddress remoteAddress) {
        ActiveChannel activeChannel = getActiveChannel(remoteAddress);
        return ChannelAccessUtils.eventLoop(activeChannel.channel());
    }

    @Override
    public Thread eventLoopThread(SocketAddress remoteAddress) throws ExecutionException, InterruptedException {
        ActiveChannel channel = getActiveChannel(remoteAddress);
        return ChannelAccessUtils.eventLoopThread(channel.channel());
    }

    private ActiveChannel getActiveChannel(SocketAddress remoteAddress) {
        if (!activeChannels.containsKey(remoteAddress)) {
            throw new NotYetConnectedException();
        }
        return activeChannels.get(remoteAddress);
    }

    private record ActiveChannel(SocketAddress remoteAddress, Channel channel,
                                 BlockingQueue<Object> readQueue) {}
}
