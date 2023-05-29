package practice.netty.client;

import practice.netty.dto.FileTransferDto;

public interface FileClient {
    void downloadFile(FileTransferDto fileTransferDto) throws Exception;
    void uploadFile(FileTransferDto fileTransferDto) throws Exception;
}
