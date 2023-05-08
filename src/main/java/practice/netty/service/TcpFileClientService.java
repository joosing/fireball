package practice.netty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.netty.client.FileClient;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

@Service
@RequiredArgsConstructor
public class TcpFileClientService implements FileClientService {
    private final FileClient fileClient;

    @Override
    public void downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws Exception {
        fileClient.downloadFile(remoteFile, localFilePath).get();
    }

    @Override
    public void uploadFile(LocalFile localFile, RemoteFile remoteFilePath) throws Exception {
        fileClient.uploadFile(localFile, remoteFilePath).get();
    }
}
