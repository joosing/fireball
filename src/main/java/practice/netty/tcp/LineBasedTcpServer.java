package practice.netty.tcp;

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
import practice.netty.tcp.handler.inbound.ClientActiveNotifier;
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static practice.netty.tcp.util.PropagateChannelFuture.propagateChannelFuture;

public class LineBasedTcpServer implements TcpServer, ClientActiveListener, ReadableQueueListener {
    private ServerBootstrap bootstrap;
    private ConcurrentHashMap<SocketAddress, Channel> activeChannelMap;
    private ConcurrentHashMap<SocketAddress, BlockingQueue<String>> channelReadQueueMap;

    public static TcpServer newServer(int port) throws ExecutionException, InterruptedException {
        TcpServer server = new LineBasedTcpServer();
        server.init();
        server.start(port).get();
        return server;
    }

    @Override
    public void init() {
        bootstrap = new ServerBootstrap();
        activeChannelMap = new ConcurrentHashMap<>();
        channelReadQueueMap = new ConcurrentHashMap<>();
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
        activeChannelMap.values().forEach(channel -> {
            var sendFuture = new CompletableFuture<Boolean>();
            channel.writeAndFlush(message).addListener((ChannelFutureListener) channelFuture -> {
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
    public String read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException {
        BlockingQueue<String> recvQueue = channelReadQueueMap.get(clientAddress);
        if (recvQueue == null) {
            return null;
        }
        return recvQueue.poll(timeout, unit);
    }

    @Override
    @Nullable
    public String read(SocketAddress clientAddress) throws InterruptedException {
        BlockingQueue<String> recvQueue = channelReadQueueMap.get(clientAddress);
        if (recvQueue == null) {
            return null;
        }
        return recvQueue.take();
    }

    @Override
    public boolean isActive(SocketAddress clientAddress) {
        return activeChannelMap.containsKey(clientAddress) &&
                channelReadQueueMap.get(clientAddress) != null;
    }

    @Override
    public void onActive(SocketAddress remoteAddress, Channel workingChannel) {
        activeChannelMap.put(remoteAddress, workingChannel);
    }

    @Override
    public void onInactive(SocketAddress remoteAddress) {
        activeChannelMap.remove(remoteAddress);
    }

    @Override
    public void onReadAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue) {
        channelReadQueueMap.put(remoteAddress, recvQueue);
    }

    @Override
    public void onReadUnavailable(SocketAddress remoteAddress) {
        channelReadQueueMap.remove(remoteAddress);
    }
}
