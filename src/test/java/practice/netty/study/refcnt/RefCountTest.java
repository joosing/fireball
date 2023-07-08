package practice.netty.study.refcnt;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

@Slf4j
public class RefCountTest {
    Channel client;
    Bootstrap clientBootstrap;
    ServerBootstrap serverBootstrap;
    NioEventLoopGroup clientWorkerGroup;
    NioEventLoopGroup serverWorkerGroup;

    @BeforeEach
    public void setUp() throws InterruptedException {
        setUpServer("0.0.0.0", 12345);
        setUpClient("127.0.0.1", 12345);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        clientWorkerGroup.shutdownGracefully().sync();
        serverWorkerGroup.shutdownGracefully().sync();
    }

    private void setUpServer(String ip, int port) throws InterruptedException {
        serverWorkerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(serverWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(ip, port)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(4))
                                .addLast(new EchoHandler());
                    }
                });
        serverBootstrap.bind().sync();
    }

    private void setUpClient(String ip, int port) throws InterruptedException {
        clientWorkerGroup = new NioEventLoopGroup();
        clientBootstrap = new Bootstrap();

        clientBootstrap.group(clientWorkerGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(4))
                                .addLast(new MyInboundHandler(1))
                                .addLast(new MyInboundHandler(2));
                    }
                });
        client = clientBootstrap.connect().sync().channel();
    }

    @Test
    public void tail() throws InterruptedException {
        var msg = Unpooled.copiedBuffer("ABCD", StandardCharsets.UTF_8);
        client.writeAndFlush(msg);
    }

    private static class EchoHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("Server Echo : {}", ((ByteBuf) msg).toString(StandardCharsets.UTF_8));
            ctx.writeAndFlush(msg);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private static class MyInboundHandler extends ChannelInboundHandlerAdapter {
        private final int id;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("Client input (handler#{}) : {}", id, ((ByteBuf) msg).toString(StandardCharsets.UTF_8));
            ByteBuf buf = (ByteBuf) msg;
            log.info("msg refCnt (handler#{}) : {}", id, buf.refCnt());
            ctx.fireChannelRead(buf);
            log.info("msg refCnt (handler#{}) : {}", id, buf.refCnt());
        }
    }
}
