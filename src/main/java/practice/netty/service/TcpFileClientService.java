package practice.netty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.netty.client.FileClient;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class TcpFileClientService implements FileClientService {
    private final FileClient fileClient;

    @Override
    public void downloadFile(RemoteFile remoteFile, LocalFile localFilePath) throws ExecutionException,
            InterruptedException {
        fileClient.downloadFile(remoteFile, localFilePath).get();
    }

    @Override
    public void uploadFile(LocalFile localFile, RemoteFile remoteFilePath) {
        fileClient.uploadFile(localFile, remoteFilePath);
    }
}
