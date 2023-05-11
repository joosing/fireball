package practice.netty.service;

import practice.netty.dto.FileTransferDto;

public interface FileClientService {
    void downloadFile(FileTransferDto fileTransferDto) throws Exception;
    void uploadFile(FileTransferDto fileTransferDto) throws Exception;
}
