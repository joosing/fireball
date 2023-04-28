package practice.netty.service;

import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

import java.util.concurrent.ExecutionException;

public interface FileClientService {
    void downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws ExecutionException, InterruptedException;
    void uploadFile(LocalFile localFile, RemoteFile remoteFilePath);
}
