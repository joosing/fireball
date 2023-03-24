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
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LineBasedTcpClient implements TcpClient, ReadableQueueListener {
    private Bootstrap bootstrap;
    private volatile Channel channel;
    @Nullable private volatile BlockingQueue<String> recvQueue;
    private Test test;

    @Override
    public void init() {
        // 부트스트랩
        bootstrap = new Bootstrap();
        // 테스트 지원용
        test = new Test();
        // 이벤트 루프 그룹
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 채널 파이프라인 구성 핸들러
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        // Inbound
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new ReadDataUpdater(LineBasedTcpClient.this))
                        // Outbound
                        .addLast(new StringEncoder())
                        .addLast(new LineAppender("\n"));
            }
        };
        // 연결 설정
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);
    }

    @Override
    public void destroy() {
        channel.eventLoop().parent().shutdownGracefully();
    }

    @Override
    public Future<Boolean> connect(String ip, int port) {
        // 타겟 주소 설정
        bootstrap.remoteAddress(ip, port);

        /* 주의! bootstrap.connect()가 반환하는 ChannelFuture 객체를 그대로 사용자에게 반환하면 프로그램이 불안정한 상태에 놓일 수 있습니다.
         * bootstrap.connect()가 반환하는 ChannelFuture 객체는 등록된 리스너가 호출되기 이전에 완료 상태가 되는데 그렇게 되면 channel이
         * 초기화 되지 않은 상태에 TcpClient 구현체를 사용자가 사용하게 될 수 있습니다. 사용자가 연결 완료를 인지한 시점 부터 즉시 통신할 수
         * 있는 안정적인 상태를 제공해야 합니다.
         */
        CompletableFuture<Boolean> connectFuture = new CompletableFuture<>();

        // 연결
        bootstrap.connect().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                channel = future.channel();
            }
            connectFuture.complete(future.isSuccess());
        });
        return connectFuture;
    }

    @Override
    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void send(String data) {
        channel.writeAndFlush(data);
    }

    @Override
    @Nullable
    public String read() throws InterruptedException {
        return read(0, TimeUnit.MILLISECONDS);
    }

    @Override
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

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
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
