package io.fireball.tcp.client;

import io.fireball.handler.inbound.ReadDataUpdater;
import io.fireball.pipeline.HandlerFactory;
import io.fireball.util.ChannelAccessUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.lang.Nullable;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DefaultTcpClient implements TcpClient {
    private Bootstrap bootstrap;
    private volatile Channel channel;
    private volatile BlockingQueue<Object> readQueue;

    @Override
    public void init(EventLoopGroup eventLoopGroup, List<HandlerFactory> pipelineFactory) {
        // 읽기 큐
        readQueue = new LinkedBlockingQueue<>();

        // 자신에게 수신 메시지를 전달해 줄 핸들러 추가
        pipelineFactory.add(
                HandlerFactory.of(() -> new ReadDataUpdater(this)));

        // 부트스트랩
        bootstrap = new Bootstrap();

        // 연결 설정
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        pipelineFactory.forEach(handlerFactory -> {
                            ch.pipeline().addLast(handlerFactory.workGroup(), handlerFactory.handler());
                        });
                    }
                });
    }

    @Override
    public ChannelFuture connect(String ip, int port) throws InterruptedException {
        bootstrap.remoteAddress(ip, port);
        var result = bootstrap.connect().sync();
        if (result.isSuccess()) {
            channel = result.channel();
        }
        return result;
    }

    @Override
    public ChannelFuture disconnect() {
        return channel.close();
    }

    @Override
    public ChannelFuture send(Object data) {
        return channel.writeAndFlush(data);
    }

    /**
     * 수신된 메시지를 읽습니다. 읽을 메시지가 없는 경우 수신될 때 까지 무한히 대기합니다.
     */
    @Override
    public Object readSync() throws InterruptedException {
        return readQueue.take();
    }

    /**
     * 수신된 메시지를 읽습니다. 읽을 메시지가 없는 경우 수신될 때 까지 timeout 시간 만큼 대기합니다.
     */
    @Override
    @Nullable
    public Object read(int timeout, TimeUnit unit) throws InterruptedException {
        return readQueue.poll(timeout, unit);
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void onReadAvailable(SocketAddress remoteAddress, Object data) {
        readQueue.add(data);
    }

    @Override
    public ChannelPipeline pipeline() {
        return ChannelAccessUtils.pipeline(channel);
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public EventLoop eventLoop() {
        return ChannelAccessUtils.eventLoop(channel);
    }

    @Override
    public Thread eventLoopThread() throws ExecutionException, InterruptedException {
        return ChannelAccessUtils.eventLoopThread(channel);
    }
}
