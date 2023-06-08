package io.fireball.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFileUploadRequest implements UserRequest {
    private final String srcFile;
    private final String dstFile;
}
