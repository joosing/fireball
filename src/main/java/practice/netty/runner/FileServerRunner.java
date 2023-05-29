package practice.netty.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import practice.netty.service.FilerServer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class FileServerRunner {
    @Value("${fireball.server.port}")
    private Integer port;
    private final FilerServer server;

    @PostConstruct
    protected void start() throws ExecutionException, InterruptedException, IOException {
        server.start(port);
    }
}
