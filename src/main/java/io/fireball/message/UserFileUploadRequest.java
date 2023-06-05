package io.fireball.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFileUploadRequest implements UserRequest {
    private final String srcFilePath;
    private final String dstFilePath;
}
