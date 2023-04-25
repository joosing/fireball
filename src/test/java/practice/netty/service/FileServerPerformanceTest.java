package practice.netty.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import practice.netty.helper.FileServerPerformanceTestHelper;
import practice.netty.util.AdvancedFileUtils;
import practice.netty.util.StopWatchUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static practice.netty.util.FileSizeUtils.megaToByte;

@Slf4j
public class FileServerPerformanceTest {
    FileServerPerformanceTestHelper helper = new FileServerPerformanceTestHelper();
    static final String localFileFormat = "local%d.dat";
    static final String remoteFileFormat = "remote%d.dat";
    static final int nClients = 1024;
    static final int megaBytes = 5;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, IOException {
        helper.setUp(nClients);
        deleteLocalFiles();
    }

    @AfterEach
    void tearDown() throws InterruptedException, IOException {
        helper.tearDown();
    }

    @Test
    void fileFetch() throws Exception {
        // Given: 테스트의 빠른 실행을 위해 서버 측 서비스 파일은 미리 생성

        // When: 다중 클라이언트, 파일 패치 요청
        StopWatch stopWatch = StopWatchUtils.start();
        var futures = new CopyOnWriteArrayList<CompletableFuture<Void>>();
        IntStream.range(0, nClients).parallel().forEach(i -> {
            var future = helper.clients().get(i).remoteFileAccessor()
                    .remote(remoteFileFormat.formatted(i))
                    .local(localFileFormat.formatted(i))
                    .printSpentTime("%d File(%,d MB)fetch time(sec): ".formatted(i, megaBytes))
                    .fetch();
            futures.add(future);
        });
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
        StopWatchUtils.stopAndPrintSeconds(stopWatch, "Total time(sec): ");

        // Then: 패치된 파일이 서버 측 파일과 일치하는지 확인
        IntStream.range(0, nClients).parallel().forEach(i -> {
            var result = AdvancedFileUtils.contentEquals(localFileFormat.formatted(i), remoteFileFormat.formatted(i));
            assertTrue(result);
        });
    }

    @Test
    void newRemoteFiles() throws IOException {
        IntStream.range(0, nClients).parallel().forEach(i -> {
            AdvancedFileUtils.newRandomContentsFile(remoteFileFormat.formatted(i), megaToByte(megaBytes));
        });
    }

    @Test
    void deleteRemoteFiles() throws IOException {
        IntStream.range(0, nClients).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(Path.of(remoteFileFormat.formatted(i)));
        });
    }

    @Test
    void deleteLocalFiles() throws IOException {
        IntStream.range(0, nClients).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(Path.of(localFileFormat.formatted(i)));
        });
    }
}
