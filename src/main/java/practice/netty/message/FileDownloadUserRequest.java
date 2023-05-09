package practice.netty.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileDownloadUserRequest implements UserMessage {
    private final String remoteFilePath;
}
