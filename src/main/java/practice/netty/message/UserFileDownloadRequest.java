package practice.netty.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFileDownloadRequest extends UserRequest {
    private final String srcFilePath;
    private final String dstFilePath;
}
