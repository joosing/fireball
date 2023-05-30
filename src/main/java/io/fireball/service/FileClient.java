package io.fireball.service;

import io.fireball.dto.FileTransferDto;

public interface FileClient {
    void downloadFile(FileTransferDto fileTransferDto) throws Exception;
    void uploadFile(FileTransferDto fileTransferDto) throws Exception;
}
