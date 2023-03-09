package practice.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.inbound.ClientActiveNotifier;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.*;

public class TcpServer implements ClientActiveEventListener, ReceiveAvailableListener {
    ServerBootstrap bootstrap;
    NioEventLoopGroup acceptGroup;
    NioEventLoopGroup workingGroup;
    ConcurrentHashMap<SocketAddress, Channel> activeChannelMap;
    ConcurrentHashMap<SocketAddress, BlockingQueue<String>> channelRecvQueueMap;

    public TcpServer() {
        bootstrap = new ServerBootstrap();
        acceptGroup = new NioEventLoopGroup();
        workingGroup = new NioEventLoopGroup();
        activeChannelMap = new ConcurrentHashMap<>();
        channelRecvQueueMap = new ConcurrentHashMap<>();
    }

    public Future<Boolean> start(int bindPort) {
        CompletableFuture<Boolean> startFuture = new CompletableFuture<>();
        bootstrap.group(acceptGroup, workingGroup)
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
                                // Outbound
                                .addLast(new StringEncoder());
                    }
                }).bind().addListener((ChannelFuture future) -> startFuture.complete(future.isSuccess()));
        return startFuture;
    }

    public void destroy() {
        acceptGroup.shutdownGracefully();
        workingGroup.shutdownGracefully();
    }

    public void send(String message) {
        activeChannelMap.values().forEach(channel -> channel.writeAndFlush(message));
    }

    /**
     * 특정 클라이언로부터 메시지를 수신합니다. 타임아웃 시간 동안 메시지를 수신할 수 없으면 Optional.empty()를 반환합니다.
     * @param clientAddress 클라이언트 주소
     * @param timeout 타임아웃 시간
     * @param unit 타임아웃 시간 단위
     * @return 수신된 메시지
     */
    public Optional<String> receive(SocketAddress clientAddress, int timeout, TimeUnit unit) throws InterruptedException {
        BlockingQueue<String> recvQueue = channelRecvQueueMap.get(clientAddress);
        if (recvQueue == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(recvQueue.poll(timeout, unit));
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
    public void onReceiveAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue) {
        channelRecvQueueMap.put(remoteAddress, recvQueue);
    }

    @Override
    public void onReceiveUnavailable(SocketAddress remoteAddress) {
        channelRecvQueueMap.remove(remoteAddress);
    }
}
