package practice.netty.study.eventloop;

import io.netty.channel.DefaultEventLoopGroup;
import org.junit.jupiter.api.Test;
import practice.netty.handler.outbound.OutboundBlockingHandler;
import practice.netty.helper.TcpLoopbackSingleClientHelper;
import practice.netty.helper.TcpLoopbackTestSetting;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ThreadModelTest extends TcpLoopbackSingleClientHelper {
    public ThreadModelTest() {
        super(TcpLoopbackTestSetting.builder()
                .serverPort(12345)
                .nClient(1)
                .serverType(CustomServerType.LINE_BASED)
                .clientType(CustomClientType.LINE_BASED)
                .build());
    }

    /**
     * 네티는 읽기와 쓰기 동작을 하나의 쓰레드(이벤트루프)로 동기적으로 처리합니다.
     * 따라서 만약 쓰기 동작에서 블락킹이 발생하면 완료될 때까지 다른 모든 동작이 멈춥니다.
     */
    @Test
    void oneThreadHandleBothReadAndWriting() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널
        client.pipeline().addLast(new OutboundBlockingHandler(3000));
        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.sendAll("status").get();

        // Then : 지연 이후 메시지 수신
        await().atLeast(2900, TimeUnit.MILLISECONDS)
                .atMost(3100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.readSync(), "status"));
    }

    /**
     * 블락킹이 발생하는 핸들러가 전체 채널을 멈추게 하는 일을 막기 위해서 해당 핸들러에 전용 이벤트루프를 할당할 수 있습니다.
     */
    @Test
    void butWeCanAssignDedicatedEventLoopForBlockingHandler() throws Exception {
        // Given : 일정 시간 블락킹된 클라이언트 채널을 전용 이벤트 루프와 함께 생성
        client.pipeline().addLast(new DefaultEventLoopGroup(), new OutboundBlockingHandler(3000));

        client.send("blocking");

        // When : 서버에서 메시지 전송
        server.sendAll("status").get();

        // Then : 즉시 메시지 수신
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(() -> Objects.equals(client.readSync(), "status"));
    }
}
