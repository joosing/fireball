package practice.netty.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import practice.netty.helper.FileServiceTestHelper;
import practice.netty.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static practice.netty.util.FileSizeUtils.megaToByte;

public class FileServerTest extends FileServiceTestHelper {
    String localFilePath = "local.txt";
    String remoteFilePath = "remote.txt";

    @Override
    @BeforeEach
    protected void setUp() throws ExecutionException, InterruptedException, IOException {
        super.setUp();
        Files.deleteIfExists(Path.of(localFilePath));
        Files.deleteIfExists(Path.of(remoteFilePath));
    }

    @Override
    @AfterEach
    protected void tearDown() throws InterruptedException, IOException {
        Files.delete(Path.of(localFilePath));
        Files.delete(Path.of(remoteFilePath));
        super.tearDown();
    }

    @Test
    void serviceFile() throws Exception {
        // Given: 서버 측, 서비스 파일 생성
        int megaBytes = 1_000;
        File remoteFile = FileUtils.newRandomContentsFile(remoteFilePath, megaToByte(megaBytes));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // When: 클라이언트 측, 파일 패치 요청
        client.remoteFileAccessor()
                .remote(remoteFilePath)
                .local(localFilePath)
                .fetch().addListener(future -> {
                    // 파일 패치 시간 측정
                    stopWatch.stop();
                    System.out.printf("File(%,d MB)fetch time: %.3f sec\n", megaBytes, stopWatch.getTotalTimeSeconds());
                }).sync();

        // Then: 패치된 파일이 서버 측 파일과 일치하는지 확인
        File localFile = new File(localFilePath);
        assertTrue(localFile.exists());
        assertTrue(org.apache.commons.io.FileUtils.contentEquals(remoteFile, localFile));
    }
}
