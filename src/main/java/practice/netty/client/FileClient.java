package practice.netty.client;

import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

import java.util.concurrent.CompletableFuture;

public interface FileClient {
    CompletableFuture<Void> downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws Exception;
    CompletableFuture<Void> uploadFile(LocalFile localFilePath, RemoteFile remoteFile) throws Exception;
}
