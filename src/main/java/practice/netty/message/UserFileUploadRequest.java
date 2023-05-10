package practice.netty.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFileUploadRequest implements UserMessage {
    private final String srcFilePath;
    private final String dstFilePath;
}
