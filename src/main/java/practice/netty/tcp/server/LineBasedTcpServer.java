package practice.netty.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.lang.Nullable;
import practice.netty.tcp.common.ReadDataListener;
import practice.netty.tcp.handler.inbound.ClientActiveNotifier;
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static practice.netty.tcp.util.PropagateChannelFuture.propagateChannelFuture;

public class LineBasedTcpServer implements TcpServer, ClientActiveListener, ReadDataListener {
    private ServerBootstrap bootstrap;
    private ConcurrentHashMap<SocketAddress, ActiveChannel> activeChannels;

    public static TcpServer newServer(int port) throws ExecutionException, InterruptedException {
        TcpServer server = new LineBasedTcpServer();
        server.init();
        server.start(port).get();
        return server;
    }

    @Override
    public void init() {
        bootstrap = new ServerBootstrap();
        activeChannels = new ConcurrentHashMap<>();
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup serviceGroup = new NioEventLoopGroup();
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        // Inbound
                        .addLast(new ClientActiveNotifier(LineBasedTcpServer.this))
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new ReadDataUpdater(LineBasedTcpServer.this))
                        // Outbound
                        .addLast(new StringEncoder())
                        .addLast(new LineAppender("\n"));
            }
        };
        bootstrap.group(acceptGroup, serviceGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);
    }

    @Override
    public Future<Boolean> shutdownGracefully() {
        Supplier<io.netty.util.concurrent.Future<?>> acceptShutdownSupplier = () -> bootstrap.config().group().shutdownGracefully();
        Supplier<io.netty.util.concurrent.Future<?>> clientsShutdownSupplier = () -> bootstrap.config().childGroup().shutdownGracefully();
        return CompletableFuture.supplyAsync(acceptShutdownSupplier)
                .thenCombine(CompletableFuture.supplyAsync(clientsShutdownSupplier),
                        (acceptShutdown, clientsShutdown) -> acceptShutdown.isSuccess() && clientsShutdown.isSuccess());

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
    public Future<Boolean> sendAll(String message) {
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

    private record ActiveChannel(SocketAddress remoteAddress, Channel channel,
                                 BlockingQueue<Object> readQueue) {
    }
}
