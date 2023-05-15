package practice.netty.helper;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT // application.properties: server.port={defined-port}
)
public class RestAssuredTest {
    @LocalServerPort
    private int definedPort;

    @BeforeEach
    protected void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = definedPort;
    }
}
