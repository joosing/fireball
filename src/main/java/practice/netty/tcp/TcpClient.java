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
import practice.netty.handler.inbound.ReceiveDataUpdater;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpClient {
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup eventLoopGroup;
    private final BlockingQueue<String> recvQueue;
    private final Test test;
    private Channel channel;

    public TcpClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        recvQueue = new LinkedBlockingQueue<>();
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
                                .addLast(new ReceiveDataUpdater(recvQueue))
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

    /**
     * 테스트를 지원하기 위한 용도의 메서드를 분리합니다.
     */
    public class Test {
        public ChannelPipeline pipeline() {
            return channel.pipeline();
        }

        public BlockingQueue<String> recvQueue() {
            return recvQueue;
        }
    }
}
