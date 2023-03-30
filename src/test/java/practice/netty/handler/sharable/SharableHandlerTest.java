package practice.netty.handler.sharable;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.TcpServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static practice.netty.tcp.client.CustomClientFactor.newConnection;
import static practice.netty.tcp.server.LineBasedTcpServer.newServer;

/*
 * Sharable 어노테이션의 특성에 대해 상세히 묘사합니다.
 *
 * 네티 프레임워크는 @Sharable 어노테이션이 붙은 핸들러를 다음과 같은 상태라고 가정합니다.
 * - 멀티쓰레드 환경에서 경합 상태(race condition)가 발생하지 않는 쓰레드 안정성을 보장한다.
 * - 하나 또는 그 이상의 파이프라인에 여러번 추가할 수 있는 핸들러이다.
 *
 * 그러나 주의해야 할 점은 실제로 핸들러가 쓰레드 안정성을 보장하는지 그리고 핸들러가 쓰레드 안정성을 보장하더라도 공유해도 되는 상태를
 * 가지는 지는 핸들러를 개발하는 개발자가 보장해 주어야 한다는 사실입니다. 네티의 메인테이너 중 한명인 노먼은 Netty in Action 책에서
 * 여러 채널의 통계 정보를 측정하는 일 외에는 @Sharable 어노테이션을 사용하지 않는 것을 권장한다고 합니다.
 * (참조 : https://netty.io/4.0/api/io/netty/channel/ChannelHandler.Sharable.html)
 */
@Slf4j
public class SharableHandlerTest {
    TcpServer server;
    CustomClient clientOne;
    CustomClient clientTwo;
    EventLoopGroup clientEventLoopGroup;

    @BeforeEach
    void beforeEach() throws Exception {
        // 비동기 테스트 프레임워크 설정
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS); // 폴링 간격

        // 서버 및 클라이언트 연결 설정
        server = newServer(12345);
        clientEventLoopGroup = new NioEventLoopGroup();
        clientOne = newConnection(CustomClientType.LINE_BASED, "localhost", 12345, clientEventLoopGroup);
        clientTwo = newConnection(CustomClientType.LINE_BASED, "localhost", 12345, clientEventLoopGroup);

        // 서버가 클라이언트와 통신 가능한 상태가 될 때까지 대기
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> server.isActive(clientOne.localAddress())
                        && server.isActive(clientTwo.localAddress()));
    }

    @AfterEach
    void afterEach() throws InterruptedException, ExecutionException {
        server.shutdownGracefully().get();
        clientEventLoopGroup.shutdownGracefully().sync();
    }

    /**
     * Sharable 어노테이션이 붙은 핸들러는 두 번 이상 파이프라인에 추가할 수 있습니다.
     */
    @Test
    void sharableHandlerTwice() throws InterruptedException {
        // When: 하나의 핸들러 인스턴스를 두 번 추가하고 메시지를 전송한다.
        SharableCountingHandler handler = new SharableCountingHandler();
        clientOne.pipeline().addLast("new", handler);
        clientTwo.pipeline().addLast("reuse", handler);
        clientOne.send("message");
        clientTwo.send("message");

        // Then: 같은 인스턴스 추가 및 동일 핸들러 두 번 호출
        ChannelHandler newHandler =  clientOne.pipeline().toMap().get("new");
        ChannelHandler reuseHandler = clientTwo.pipeline().toMap().get("reuse");
        assertSame(newHandler, reuseHandler);
        await().until(() -> handler.getCount() == 2);
    }

    /**
     * Sharable 어노테이션이 붙은 핸들러는 두 번 이상 파이프라인에 추가할 수 있습니다.
     */
    @Test
    void unsafeSharableHandlerTwice() throws InterruptedException {
        // When: 하나의 핸들러 인스턴스를 두 번 추가하고 메시지를 전송한다.
        SharableThreadUnsafeCountingHandler handler = new SharableThreadUnsafeCountingHandler();
        clientOne.pipeline().addLast("new", handler);
        clientTwo.pipeline().addLast("reuse", handler);
        clientOne.send("message");
        clientTwo.send("message");

        // Then: 같은 인스턴스 추가 및 동일 핸들러 두 번 호출
        ChannelHandler newHandler =  clientOne.pipeline().toMap().get("new");
        ChannelHandler reuseHandler = clientTwo.pipeline().toMap().get("reuse");
        assertSame(newHandler, reuseHandler);
        await().until(() -> handler.getCount() == 2);
    }

    /**
     * Sharable 어노테이션이 붙지 않은 핸들러를 두 번 이상 파이프라인에 추가하려 하면 작업이 거절되고 예외가 발생합니다.
     */
    @Test
    void UnsharableHandlerTwice() throws InterruptedException {
        UnsharableCountingHandler handler = new UnsharableCountingHandler();
        assertThrows(ChannelPipelineException.class, () -> {
            clientOne.pipeline().addLast(handler);
            clientTwo.pipeline().addLast(handler);
        });
    }
}
