package practice.netty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import practice.netty.dto.FileTransferDto;
import practice.netty.eventloop.ClientEventLoopGroupManager;
import practice.netty.message.UserFileDownloadRequest;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserRequest;
import practice.netty.pipeline.PipelineFactory;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
public class TcpFileClient implements FileClient {
    private final ClientEventLoopGroupManager eventLoopGroupManager;
    private final PipelineFactory pipelineFactory;

    @Autowired
    public TcpFileClient(ClientEventLoopGroupManager eventLoopGroupManager,
                         @Qualifier("tcpFileClientPipelineFactory") PipelineFactory pipelineFactory) {
        this.eventLoopGroupManager = eventLoopGroupManager;
        this.pipelineFactory = pipelineFactory;
    }

    @Override
    public void downloadFile(FileTransferDto fileTransferDto) throws ExecutionException
            , InterruptedException, TimeoutException {
        var localFile = fileTransferDto.getLocal();
        var remoteFile = fileTransferDto.getRemote();
        var downloadRequest = UserFileDownloadRequest.builder()
                .srcFilePath(remoteFile.getFilePath())
                .dstFilePath(localFile.getFilePath())
                .build();
        requestTemplate(downloadRequest, remoteFile.getIp(), remoteFile.getPort());
    }

    @Override
    public void uploadFile(FileTransferDto fileTransferDto) throws ExecutionException,
            InterruptedException, TimeoutException {
        var localFile = fileTransferDto.getLocal();
        var remoteFile = fileTransferDto.getRemote();
        var uploadRequest = UserFileUploadRequest.builder()
                .srcFilePath(localFile.getFilePath())
                .dstFilePath(remoteFile.getFilePath())
                .build();
        requestTemplate(uploadRequest, remoteFile.getIp(), remoteFile.getPort());
    }

    private void requestTemplate(UserRequest request, String ip, int port) throws ExecutionException
            , InterruptedException, TimeoutException {
        // Configure channel pipeline
        var pipelineFactory = this.pipelineFactory.get();

        // Make a connection
        TcpClient tcpClient = new DefaultTcpClient();
        tcpClient.init(eventLoopGroupManager.channelIo(), pipelineFactory);
        tcpClient.connect(ip, port).get();

        // Set to close the connection when the response is complete
        request.responseFuture().thenRunAsync(() -> {
            if (tcpClient.isActive()) {
                tcpClient.disconnect();
            }
        });

        // Send a request
        tcpClient.send(request);

        // Wait for the response
        try {
            // If there is no response for a period of time, an SererNotResponseException is thrown.
            request.responseFuture().get();
        } finally {
            tcpClient.disconnect();
        }
    }
}
