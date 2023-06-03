package io.fireball.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class FileTransferDto {
    private final String localFile;
    private final String remoteIp;
    private final int remotePort;
    private final String remoteFile;
}
