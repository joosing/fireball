package practice.netty.tcp;


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
import practice.netty.handler.outbound.OutboundDelayHandler;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("TCP server-client simple test")
public class SimpleTcpTest {
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
                                .addLast(new Logger("Server", true));
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
                                .addLast(new Logger("Client", true));
                    }
                });
        clientChannel = clientBootstrap.connect().sync().channel();
    }

    @Test
    @SneakyThrows
    @DisplayName("When client sends a command, Then server react with response and client receive it")
    void simpleResponseTest() {
        // Given : Connected server and client

        // When : Client sends a command
        clientChannel.writeAndFlush("ABCD");

        // Then : Server sends a response and client receive it
        String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(fixedResponse, response);
    }

    @Test
    @SneakyThrows
    @DisplayName("When Server sends 10 commands, Then client receive 10 responses")
    void multipleReceiveTest() {
        // Given : Connected server and client

        // When : Server sends 10 commands
        Channel serverServiceChannel = activeServerChannelMap.get(clientChannel.localAddress());
        for (int i = 0; i < 10; i++) {
            serverServiceChannel.writeAndFlush(String.format("RES%d", i));
        }

        // Then : Client receive 10 responses
        for (int i = 0; i < 10; i++) {
            String response = clientResponseQueue.poll(100, TimeUnit.MILLISECONDS);
            Assertions.assertEquals(String.format("RES%d", i), response);
        }
    }
}

