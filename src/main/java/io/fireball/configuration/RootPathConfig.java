package io.fireball.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class RootPathConfig {
    @Value("${fireball.client.file.root-path}")
    private String clientRootPath;
    @Value("${fireball.server.file.root-path}")
    private String serverRootPath;

    @PostConstruct
    public void configure() throws IOException {
        if (StringUtils.isBlank(clientRootPath)) {
            throw new RuntimeException("You must set the client side root directory." +
                    "For example: -Dfireball.client.file.root-path=/some/path)");
        }
        if (StringUtils.isBlank(serverRootPath)) {
            throw new RuntimeException("You must set the server side root directory." +
                    "For example: -Dfireball.server.file.root-path=/some/path)");
        }

        FileUtils.forceMkdir(new File(clientRootPath));
        FileUtils.forceMkdir(new File(serverRootPath));
    }

    private static void logAndThrowException(String message) {
        log.info(message);
        throw new RuntimeException(message);
    }
}
