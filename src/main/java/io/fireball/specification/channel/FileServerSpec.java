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
    @Value("${fireball.server.root-path}")
    private String rootPath;
    private final int idleTimeSec = 3;
    private final int chunkSize = 1024 * 1024 * 5;
    private final int nBossMaxThread = 0; // zero value is netty's default setting (CPU core * 2)
    private final int nChannelIoMaxThread = 0;
    private final int nFileStoreMaxThread = 0;
}
