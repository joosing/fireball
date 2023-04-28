package practice.netty.client;

import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FileClient {
    CompletableFuture<Void> downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws ExecutionException,
            InterruptedException;
    CompletableFuture<Void> uploadFile(LocalFile localFilePath, RemoteFile remoteFile);
}
