package practice.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import practice.netty.handler.duplex.Logger;
import practice.netty.handler.inbound.ActiveServerChannelUpdater;
import practice.netty.handler.inbound.ReceiveDataUpdater;
import practice.netty.handler.inbound.ServerResponseHandler;
import practice.netty.handler.outbound.BlockingWriteHandler;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class test {
    // 서버
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    NioEventLoopGroup serverAcceptGroup = new NioEventLoopGroup();
    NioEventLoopGroup serverServiceGroup = new NioEventLoopGroup();
    ConcurrentHashMap<SocketAddress, Channel> activeServerChannelMap = new ConcurrentHashMap<>();

    // 클라이언트
    Bootstrap clientBootstrap = new Bootstrap();
    NioEventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();
    BlockingQueue<String> clientResponseQueue = new LinkedBlockingQueue<>();
    Channel clientChannel;

    // 테스트 데이터
    String fixedResponse = "RESP";

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        serverSetupAndStart();
        clientSetupAndConnect();
        waitForServerServiceActive();
    }

    private void waitForServerServiceActive() throws InterruptedException {
        Thread.sleep(100);
    }

    @AfterEach
    @SneakyThrows
    public void afterEach() {
        clientEventLoopGroup.shutdownGracefully();
        serverServiceGroup.shutdownGracefully();
        serverAcceptGroup.shutdownGracefully();
    }

    @SneakyThrows
    private void serverSetupAndStart() {
        serverBootstrap.group(serverAcceptGroup, serverServiceGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress("0.0.0.0", 12345)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // Inbound
                                .addLast(new ActiveServerChannelUpdater(activeServerChannelMap))
                                .addLast(new FixedLengthFrameDecoder(4))
                                .addLast(new StringDecoder())
                                .addLast(new ServerResponseHandler(fixedResponse))

                                // Outbound
                                .addLast(new StringEncoder())

                                // Duplex
                                .addLast(new Logger("Server"));
                    }
                });
        serverBootstrap.bind().sync();
    }

    @SneakyThrows
    private void clientSetupAndConnect() {
        clientBootstrap.group(clientEventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress("127.0.0.1", 12345)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // Inbound
                                .addLast(new FixedLengthFrameDecoder(4))
                                .addLast(new StringDecoder())
                                .addLast(new ReceiveDataUpdater(clientResponseQueue))

                                // Outbound
                                .addLast(new StringEncoder())

                                // Duplex
                                .addLast(new Logger("Client"));
                    }
                });
        clientChannel = clientBootstrap.connect().sync().channel();
    }

    @Test
    @SneakyThrows
    void simpleResponseTest() {
        clientChannel.writeAndFlush("ABCD");
        String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(fixedResponse, response);
    }

    @Test
    @SneakyThrows
    void multipleReceiveTest() {
        // Given : 연결된 서버, 클라이언트

        // When : 서버에서 10개 메시지 전송
        Channel serverServiceChannel = activeServerChannelMap.get(clientChannel.localAddress());
        for (int i = 0; i < 10; i++) {
            serverServiceChannel.writeAndFlush(String.format("RES%d", i));
        }

        // Then : 클라이언트에서 10개 메시지 수신
        for (int i = 0; i < 10; i++) {
            String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
            Assertions.assertEquals(String.format("RES%d", i), response);
        }
    }

    @Test
    @SneakyThrows
    void blockingSideEffectTest() {
        // Given : Blocking 동작을 가진 Handler 추가
        clientChannel.pipeline().addLast(new BlockingWriteHandler());

        // When : 클라이언트에서 메시지 1개 전송(Blocking), 서버에서 10개 메시지 전송
        clientChannel.writeAndFlush("ABCD");

        Channel serverServiceChannel = activeServerChannelMap.get(clientChannel.localAddress());
        for (int i = 0; i < 10; i++) {
            serverServiceChannel.writeAndFlush(String.format("RES%d", i));
        }

        // Then : 클라이언트에서 10개 메시지 수신
        for (int i = 0; i < 10; i++) {
            String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
            Assertions.assertNull(response);
        }
    }

    @Test
    @SneakyThrows
    void TakeAwayBlockingSideEffectTest() {
        // Given : Blocking 동작을 가진 Handler 독립적인 쓰레드로 처리
        clientChannel.pipeline().addLast(new DefaultEventLoopGroup(), new BlockingWriteHandler());

        // When : 클라이언트에서 메시지 1개 전송(Blocking), 서버에서 10개 메시지 전송
        clientChannel.writeAndFlush("ABCD");

        Channel serverServiceChannel = activeServerChannelMap.get(clientChannel.localAddress());
        for (int i = 0; i < 10; i++) {
            serverServiceChannel.writeAndFlush(String.format("RES%d", i));
        }

        // Then : 클라이언트에서 10개 메시지 수신
        for (int i = 0; i < 10; i++) {
            String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
            Assertions.assertEquals(String.format("RES%d", i), response);
        }
    }
}