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
import practice.netty.handler.inbound.ReadDataUpdater;
import practice.netty.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TcpClient implements ReadableQueueListener {
    private final Bootstrap bootstrap;
    private Channel channel;
    @Nullable private volatile BlockingQueue<String> recvQueue;
    private final Test test;

    public TcpClient() {
        bootstrap = new Bootstrap();
        test = new Test();
    }

    public Future<Boolean> connect(String ip, int port) {
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // Inbound
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new ReadDataUpdater(TcpClient.this))
                                // Outbound
                                .addLast(new StringEncoder())
                                .addLast(new LineAppender("\n"));
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

    @Nullable
    public String read() throws InterruptedException {
        return read(0, TimeUnit.MILLISECONDS);
    }

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

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    public void destroy() {
        channel.eventLoop().parent().shutdownGracefully();
    }

    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    public Test test() {
        return test;
    }

    @Override
    public void onReadAvailable(SocketAddress remoteAddress, BlockingQueue<String> recvQueue) {
        this.recvQueue = recvQueue;
    }

    @Override
    public void onReadUnavailable(SocketAddress remoteAddress) {
        recvQueue = null;
    }

    /**
     * 테스트를 지원하기 위한 용도의 메서드를 분리합니다.
     */
    public class Test {
        public ChannelPipeline pipeline() {
            return channel.pipeline();
        }
        public Channel channel() {
            return channel;
        }
    }
}
