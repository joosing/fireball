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
import practice.netty.handler.inbound.ActiveServerChannelUpdater;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class TcpServer {
    ServerBootstrap bootstrap;
    NioEventLoopGroup acceptGroup;
    NioEventLoopGroup workingGroup;
    ConcurrentHashMap<SocketAddress, Channel> activeChannelMap;

    public TcpServer() {
        bootstrap = new ServerBootstrap();
        acceptGroup = new NioEventLoopGroup();
        workingGroup = new NioEventLoopGroup();
        activeChannelMap = new ConcurrentHashMap<>();
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
                                .addLast(new ActiveServerChannelUpdater(activeChannelMap))
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
}
