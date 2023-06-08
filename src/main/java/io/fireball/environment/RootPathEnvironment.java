package io.fireball.environment;

import lombok.Getter;
import lombok.experimental.Accessors;
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
@Getter
@Accessors(fluent = true)
public class RootPathEnvironment {
    @Value("${file.client.root}")
    private String clientRootPath;
    @Value("${file.server.root}")
    private String serverRootPath;

    @PostConstruct
    public void configure() throws IOException {
        if (StringUtils.isBlank(clientRootPath)) {
            throw new RuntimeException("You must set the client side root directory." +
                    "Like that: -Dfile.client.root=/some/path)");
        }
        if (StringUtils.isBlank(serverRootPath)) {
            throw new RuntimeException("You must set the server side root directory." +
                    "Like that: -Dfile.server.root=/some/path)");
        }

        FileUtils.forceMkdir(new File(clientRootPath));
        FileUtils.forceMkdir(new File(serverRootPath));
    }
}
