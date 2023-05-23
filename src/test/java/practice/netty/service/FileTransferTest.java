package practice.netty.service;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import practice.netty.helper.RestAssuredTest;
import practice.netty.specification.channel.ChannelSpecProvider;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static practice.netty.util.AdvancedFileUtils.*;


@Slf4j
public class FileTransferTest extends RestAssuredTest {
    @Autowired
    ChannelSpecProvider channelSpecProvider;
    String clientFileName;
    String serverFileName;
    String clientFilePath;
    String serverFilePath;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        clientFileName = "local.dat";
        serverFileName = "remote.dat";
        clientFilePath = channelSpecProvider.client().rootPath() + clientFileName;
        serverFilePath = channelSpecProvider.server().rootPath() + serverFileName;
    }

    @AfterEach
    public void tearDown() {
        deleteIfExists(clientFilePath);
        deleteIfExists(serverFilePath);
    }

    @Test
    void fileDownload() throws Exception {
        // Given: 서버 서비스 파일 생성, 클라이언트 다운로드 파일 삭제
        LocalDateTime startTime = LocalDateTime.now();
        newRandomContentsFile(serverFilePath, 256);
        deleteIfExists(clientFilePath);

        // When: 클라이언트 측, 파일 패치 요청
        RestAssured.
                given()
                    .contentType("application/json")
                    .body("""
                            {
                                "local": {
                                    "filePath": "%s"
                                },
                                "remote": {
                                    "ip": "%s",
                                    "port": "%s",
                                    "filePath": "%s"
                                }
                            }
                            """.formatted(clientFileName, "127.0.0.1", 12345, serverFileName))
                .when()
                    .post("/download")
                .then()
                    .assertThat()
                    .statusCode(200);

        // Then: 패치된 파일이 테스트 시작 이후 수정되었고, 서버 측 파일과 내용이 일치하는지 확인
        var lastModifiedTime = getLastModifiedTime(serverFilePath);
        assertTrue(startTime.isBefore(lastModifiedTime) || startTime.isEqual(lastModifiedTime));
        assertTrue(contentEquals(clientFilePath, serverFilePath));
    }

    @Test
    void fileUpload() throws Exception {
        // Given: 랜덤한 컨텐츠를 담은, 업로드 대상 파일 생성
        LocalDateTime startTime = LocalDateTime.now();
        newRandomContentsFile(clientFilePath, 256);
        deleteIfExists(serverFilePath);

        // When: 파일 업로드 요청
        RestAssured.
                given()
                    .contentType("application/json")
                    .body("""
                                {
                                    "local": {
                                        "filePath": "%s"
                                    },
                                    "remote": {
                                        "ip": "%s",
                                        "port": "%s",
                                        "filePath": "%s"
                                    }
                                }
                                """.formatted(clientFileName, "127.0.0.1", 12345, serverFileName))
                .when()
                  .post("/upload")
                .then()
                    .assertThat()
                    .statusCode(200);

        // Then: 업로드된 파일이 테스트 시작 이후 수정되었고, 원본 파일과 업로드된 파일이 일치한다.
        var lastModifiedTime = getLastModifiedTime(serverFilePath);
        assertTrue(lastModifiedTime.isAfter(startTime) || lastModifiedTime.isEqual(startTime));
        assertTrue(contentEquals(serverFilePath, clientFilePath));
    }
}
