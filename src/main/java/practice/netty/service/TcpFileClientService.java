package practice.netty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.netty.client.FileClient;
import practice.netty.dto.FileTransferDto;

@Service
@RequiredArgsConstructor
public class TcpFileClientService implements FileClientService {
    private final FileClient fileClient;

    @Override
    public void downloadFile(FileTransferDto fileTransferDto) throws Exception {
        fileClient.downloadFile(fileTransferDto).get();
    }

    @Override
    public void uploadFile(FileTransferDto fileTransferDto) throws Exception {
        fileClient.uploadFile(fileTransferDto).get();
    }
}
