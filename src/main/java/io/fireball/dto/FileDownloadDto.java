package io.fireball.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@ToString
@Accessors(fluent = true)
public class FileDownloadDto {
    private final RemoteFileDto source;
    private final LocalFileDto destination;
}
