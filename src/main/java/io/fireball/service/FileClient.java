package io.fireball.service;

import io.fireball.dto.FileDownloadDto;
import io.fireball.dto.FileUploadDto;

public interface FileClient {
    void downloadFile(FileDownloadDto spec) throws Exception;
    void uploadFile(FileUploadDto spec) throws Exception;
}
