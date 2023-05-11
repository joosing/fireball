package practice.netty.specification.channel;

import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("FieldMayBeStatic")
@Getter
@Accessors(fluent = true)
public class FileClientSpec {
    private final String rootPath = "./";
}
