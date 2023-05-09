package practice.netty.service;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static practice.netty.util.AdvancedFileUtils.*;
import static practice.netty.util.FileSizeUtils.megaToByte;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // application.properties: server.port={defined-port}
@Slf4j
public class FileUploadTest {
    static final String LOCAL_FILE_PATH = "local.dat";
    static final String REMOTE_FILE_PATH = "remote.dat";
    static final String BASE_URI = "http://localhost";

    @LocalServerPort
    private int definedPort;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, IOException {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = definedPort;

        deleteIfExists(LOCAL_FILE_PATH);
    }

    @Test
    void fileUpload() throws Exception {
        // Given: 랜덤한 컨텐츠를 담은, 업로드 대상 파일 생성
        LocalDateTime startTime = LocalDateTime.now();
        File serviceFile = newRandomContentsFile(LOCAL_FILE_PATH, megaToByte(5));

        // When: 파일 업로드 요청
        RestAssured.
                given()
                    .contentType("application/json")
                    .body("""
                            {
                                "path": "%s"
                            }
                            """.formatted(LOCAL_FILE_PATH))
                .when()
                    .post("/files/remote/{ip}/{port}/{path}", "127.0.0.1", 12345, REMOTE_FILE_PATH)
                .then()
                    .assertThat()
                    .statusCode(200);

        // Then: 업로드된 파일이 테스트 시작 이후 수정되었고, 원본 파일과 업로드된 파일이 일치한다.
        var lastModifiedTime = getLastModifiedTime(REMOTE_FILE_PATH);
        assertTrue(lastModifiedTime.isAfter(startTime));
        assertTrue(contentEquals(REMOTE_FILE_PATH, LOCAL_FILE_PATH));
    }
}
