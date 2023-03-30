package practice.netty.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.lang.Nullable;
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

public class LineBasedTcpServer implements TcpServer {
    private ServerBootstrap bootstrap;
    private ConcurrentHashMap<SocketAddress, ActiveChannel> activeChannels;

    public static TcpServer newServer(int port, EventLoopGroup bossGroup, EventLoopGroup childGroup) throws ExecutionException, InterruptedException {
        TcpServer server = new LineBasedTcpServer();

        List<Supplier<ChannelHandler>> childHandlers = new ArrayList<>();
        childHandlers.add(() -> new LineBasedFrameDecoder(1024));
        childHandlers.add(() -> new StringDecoder());
        childHandlers.add(() -> new StringEncoder());
        childHandlers.add(() -> new LineAppender("\n"));

        server.init(bossGroup, childGroup, childHandlers);
        server.start(port).get();
        return server;
    }

    @Override
    public void init(EventLoopGroup bossGroup, EventLoopGroup childGroup, List<Supplier<ChannelHandler>> childHandlers) {

        // 자신에게 클라이언트 접속을 알리도록 알림 핸들러를 추가
        childHandlers.add(() -> new ClientActiveNotifier(this));
        // 자신에게 클라이언트로부터 데이터를 읽었음을 알리도록 알림 핸들러를 추가
        childHandlers.add(() -> new ReadDataUpdater(this));

        bootstrap = new ServerBootstrap();
        activeChannels = new ConcurrentHashMap<>();
        bootstrap.group(bossGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        childHandlers.forEach(supplier -> {
                            ch.pipeline().addLast(supplier.get());
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
