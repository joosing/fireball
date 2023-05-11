package practice.netty.specification.channel;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("FieldMayBeStatic")
public class FileServerSpec {
    private final int chunkSize = 1024 * 1024 * 5;
    private final String rootPath = "./";
}
