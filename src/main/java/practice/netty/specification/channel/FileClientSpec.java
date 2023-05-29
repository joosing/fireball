package practice.netty.specification.channel;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("ALL")
@Getter
@Accessors(fluent = true)
@Component
public class FileClientSpec {
    @Value("${fireball.client.file.root-path}")
    private String rootPath;
    private final int idleTimeSec = 10;
}
