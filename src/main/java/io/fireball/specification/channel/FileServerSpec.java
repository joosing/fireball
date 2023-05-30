package io.fireball.specification.channel;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("FieldMayBeStatic")
@Getter
@Accessors(fluent = true)
@Component
public class FileServerSpec {
    private final int idleTimeSec = 10;
    private final int chunkSize = 1024 * 1024 * 5;
    @Value("${fireball.server.file.root-path}")
    private String rootPath;
}
