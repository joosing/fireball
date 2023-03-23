package practice.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.lang.Nullable;
import practice.netty.handler.inbound.ClientActiveNotifier;
import practice.netty.handler.inbound.ReadDataUpdater;
import practice.netty.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.util.concurrent.*;

public class LineBasedTcpServer implements TcpServer, ClientActiveListener, ReadableQueueListener {
    private ServerBootstrap bootstrap;
    private ConcurrentHashMap<SocketAddress, Channel> activeChannelMap;
    private ConcurrentHashMap<SocketAddress, BlockingQueue<String>> channelReadQueueMap;

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
    public void destroy() {
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();
    }

    @Override
    public Future<Boolean> start(int bindPort) {
        bootstrap.localAddress("0.0.0.0", bindPort);
        CompletableFuture<Boolean> startFuture = new CompletableFuture<>();
        ChannelFutureListener bindFutureListener = (ChannelFuture future) -> startFuture.complete(future.isSuccess());
        bootstrap.bind().addListener(bindFutureListener);
        return startFuture;
    }

    @Override
    public void sendAll(String message) {
        activeChannelMap.values().forEach(channel -> channel.writeAndFlush(message));
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
        return read(clientAddress, 0, TimeUnit.MILLISECONDS);
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
