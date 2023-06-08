package io.fireball.runner;

import io.fireball.service.FilerServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class FileServerRunner {
    @Value("${file.server.port}")
    private Integer port;
    private final FilerServer server;

    @PostConstruct
    protected void start() throws ExecutionException, InterruptedException, IOException {
        server.start(port);
    }
}
