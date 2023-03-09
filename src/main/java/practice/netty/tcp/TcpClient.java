package practice.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.lang.Nullable;
import practice.netty.handler.inbound.ReceiveDataUpdater;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TcpClient implements ReceiveAvailableListener {
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup eventLoopGroup;
    @Nullable private BlockingQueue<String> recvQueue;
    private final Test test;
    private Channel channel;

    public TcpClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        test = new Test();
    }

    public Future<Boolean> connect(String ip, int port) {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // Inbound
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new ReceiveDataUpdater(TcpClient.this))
                                // Outbound
                                .addLast(new StringEncoder());
                    }
                });

        CompletableFuture<Boolean> connectFuture = new CompletableFuture<>();
        bootstrap.connect().addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
            }
            connectFuture.complete(channelFuture.isSuccess());
        });
        return connectFuture;
    }

    public void send(String data) {
        channel.writeAndFlush(data);
    }

    public Optional<String> receive(int timeout, TimeUnit unit) throws InterruptedException {
        if (recvQueue == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(recvQueue.poll(timeout, unit));
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    public void destroy() {
        eventLoopGroup.shutdownGracefully();
    }

    public Test test() {
        return test;
    }

    @Override
    public void onReceiveAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue) {
        this.recvQueue = recvQueue;
    }

    @Override
    public void onReceiveUnavailable(SocketAddress remoteAddress) {
        recvQueue = null;
    }

    /**
     * 테스트를 지원하기 위한 용도의 메서드를 분리합니다.
     */
    public class Test {
        public ChannelPipeline pipeline() {
            return channel.pipeline();
        }
    }
}
