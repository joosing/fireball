package io.fireball.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class FileUploadDto {
    private final LocalFileDto source;
    private final RemoteFileDto destination;
}
