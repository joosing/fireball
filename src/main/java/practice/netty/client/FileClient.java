package practice.netty.client;

import practice.netty.dto.FileTransferDto;

import java.util.concurrent.CompletableFuture;

public interface FileClient {
    CompletableFuture<Void> downloadFile(FileTransferDto fileTransferDto) throws Exception;
    CompletableFuture<Void> uploadFile(FileTransferDto fileTransferDto) throws Exception;
}
