package practice.netty.file;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practice.netty.helper.TcpLoopBackTestHelper;
import practice.netty.tcp.client.CustomClient;
import practice.netty.tcp.client.CustomClientType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static org.awaitility.Awaitility.await;

@Slf4j
public class SendFileTest extends TcpLoopBackTestHelper {
    CustomClient client;

    public SendFileTest() {
        super(12345, 1, CustomClientType.DEFAULT);
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        client = clients.get(0);
    }

    @Test
    void sendFileThroughEmptyPipeline() throws Exception {
        // Given: 파일 생성
        String path = "C:/Users/gkswn/Desktop/test.txt";
        String content = "Hello server.\n";
        File file = newFile(path, content);
        DefaultFileRegion fileRegion = new DefaultFileRegion(file, 0, file.length());

        // When: 빈 파이프라인으로 DefaultFileRegion 타입 객체 전송 요청
        client.send(fileRegion);

        // 서버에서 파일 내용을 메시지로 수신
        await().until(() -> Objects.equals(server.readSync(client.localAddress()), "Hello server."));
    }

    private static File newFile(String path, String content) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
            return new File(path);
        }
    }
}
