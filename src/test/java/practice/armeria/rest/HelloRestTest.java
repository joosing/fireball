package practice.armeria.rest;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.logging.LoggingService;

public class HelloRestTest {

    @Test
    void helloRestServer() throws InterruptedException {
        final Server server = Server.builder()
                                    .http(8080)
                                    .service("/hello/:name",
                                       (ctx, req) -> HttpResponse.of("Hello, %s!",
                                                                     Objects.requireNonNull(
                                                                             ctx.pathParam("name"))))
                                    .decorator(LoggingService.newDecorator())
                                    .build();
        server.start().join();
        Thread.sleep(1000 * 60);
    }
}
