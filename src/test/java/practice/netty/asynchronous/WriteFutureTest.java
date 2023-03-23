package practice.netty.asynchronous;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.Test;
import practice.netty.handler.TcpLoopBackTest;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class WriteFutureTest extends TcpLoopBackTest {
    /**
     * write() 메서드를 통한 전송 요청은 flush() 될 때 완료될 수 있다.
     */
    @Test
    void writeFutureComplete_whenFlushed() throws Exception {
        // When: 서버로 메시지 write만 수행
        Channel channel = client.test().channel();
        ChannelFuture future = channel.write("command");

        // Then: 1초간 future가 완료되지 않음
        await().during(1000, TimeUnit.MILLISECONDS)
                .until(() ->
                        !future.isDone() &&
                        !Objects.equals(server.read(client.localAddress()), "command")
                );

        // When: 서버로 메시지 flush
        channel.flush();

        // Then: 즉시 future 완료
        await().atMost(100, TimeUnit.MILLISECONDS)
                .until(() ->
                        future.isDone() &&
                        Objects.equals(server.read(client.localAddress()), "command")
                );
    }
}
