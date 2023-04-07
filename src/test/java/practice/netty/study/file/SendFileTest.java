package practice.netty.study.file;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import practice.netty.helper.TcpLoopbackSingleClientHelper;
import practice.netty.helper.TcpLoopbackTestSetting;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static org.awaitility.Awaitility.await;

@Slf4j
public class SendFileTest extends TcpLoopbackSingleClientHelper {
    public SendFileTest() {
        super(TcpLoopbackTestSetting.builder()
                .serverPort(12345)
                .nClient(1)
                .serverType(CustomServerType.LINE_BASED)
                .clientType(CustomClientType.DEFAULT)
                .build());
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
