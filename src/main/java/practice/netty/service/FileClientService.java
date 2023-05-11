package practice.netty.service;

import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

public interface FileClientService {
    void downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws Exception;
    void uploadFile(LocalFile localFile, RemoteFile remoteFilePath) throws Exception;
}
