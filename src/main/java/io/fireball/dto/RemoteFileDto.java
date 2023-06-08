package io.fireball.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public class RemoteFileDto {
    private final String ip;
    private final int port;
    private final String file;
}
