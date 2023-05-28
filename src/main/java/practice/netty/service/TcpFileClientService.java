package practice.netty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.netty.client.FileClient;
import practice.netty.dto.FileTransferDto;
import practice.netty.util.StopWatchUtils;

@Service
@RequiredArgsConstructor
public class TcpFileClientService implements FileClientService {
    private final FileClient fileClient;

    @Override
    public void downloadFile(FileTransferDto fileTransferDto) throws Exception {
        var stopWatch = StopWatchUtils.start();
        fileClient.downloadFile(fileTransferDto).get();
        StopWatchUtils.stopAndPrintSeconds(stopWatch, "downloadFile:");
    }

    @Override
    public void uploadFile(FileTransferDto fileTransferDto) throws Exception {
        var stopWatch = StopWatchUtils.start();
        fileClient.uploadFile(fileTransferDto).get();
        StopWatchUtils.stopAndPrintSeconds(stopWatch, "upload:");
    }
}
