package practice.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static practice.netty.tcp.util.PropagateChannelFuture.propagateChannelFuture;

public class DefaultTcpClient implements TcpClient {
    private Bootstrap bootstrap;
    private volatile Channel channel;

    @Override
    public void init(EventLoopGroup eventLoopGroup, ChannelInitializer<Channel> channelInitializer) {
        // 부트스트랩
        bootstrap = new Bootstrap();
        // 연결 설정
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);
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
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        // 연결
        bootstrap.connect().addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
            }
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    @Override
    public Future<Boolean> disconnect() {
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        channel.close().addListener((ChannelFutureListener) channelFuture -> {
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
    }

    @Override
    public Future<Boolean> send(Object data) {
        CompletableFuture<Boolean> userFuture = new CompletableFuture<>();
        channel.writeAndFlush(data).addListener((ChannelFutureListener) channelFuture -> {
            propagateChannelFuture(userFuture, channelFuture);
        });
        return userFuture;
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
    public Channel channel() {
        return channel;
    }
}
