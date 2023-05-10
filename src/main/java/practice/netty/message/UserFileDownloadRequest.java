package practice.netty.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFileDownloadRequest implements UserMessage {
    private final String remoteFilePath;
}
