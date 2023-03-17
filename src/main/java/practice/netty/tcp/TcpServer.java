package practice.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
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

public class TcpServer implements ClientActiveListener, ReadableQueueListener {
    ServerBootstrap bootstrap;
    ConcurrentHashMap<SocketAddress, Channel> activeChannelMap;
    ConcurrentHashMap<SocketAddress, BlockingQueue<String>> channelReadQueueMap;

    public TcpServer() {
        bootstrap = new ServerBootstrap();
        activeChannelMap = new ConcurrentHashMap<>();
        channelReadQueueMap = new ConcurrentHashMap<>();
    }

    public Future<Boolean> start(int bindPort) {
        CompletableFuture<Boolean> startFuture = new CompletableFuture<>();
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup serviceGroup = new NioEventLoopGroup();
        bootstrap.group(acceptGroup, serviceGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress("0.0.0.0", bindPort)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // Inbound
                                .addLast(new ClientActiveNotifier(TcpServer.this))
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new ReadDataUpdater(TcpServer.this))
                                // Outbound
                                .addLast(new StringEncoder())
                                .addLast(new LineAppender("\n"));
                    }
                }).bind().addListener((ChannelFuture future) -> startFuture.complete(future.isSuccess()));
        return startFuture;
    }

    public void destroy() {
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();
    }

    public void send(String message) {
        activeChannelMap.values().forEach(channel -> channel.writeAndFlush(message));
    }

    @Nullable
    public String read(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException {
        BlockingQueue<String> recvQueue = channelReadQueueMap.get(clientAddress);
        if (recvQueue == null) {
            return null;
        }
        return recvQueue.poll(timeout, unit);
    }

    @Nullable
    public String read(SocketAddress clientAddress) throws InterruptedException {
        return read(clientAddress, 0, TimeUnit.MILLISECONDS);
    }

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
