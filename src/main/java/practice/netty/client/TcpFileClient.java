package practice.netty.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import practice.netty.dto.FileTransferDto;
import practice.netty.eventloop.ClientEventLoopGroupManager;
import practice.netty.message.UserFileDownloadRequest;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserRequest;
import practice.netty.pipeline.PipelineManager;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class TcpFileClient implements FileClient {
    private final ClientEventLoopGroupManager eventLoopGroupManager;
    private final PipelineManager pipelineManager;

    @Autowired
    public TcpFileClient(ClientEventLoopGroupManager eventLoopGroupManager,
                         @Qualifier("tcpFileClientPipelineManager") PipelineManager pipelineManager ) {
        this.eventLoopGroupManager = eventLoopGroupManager;
        this.pipelineManager = pipelineManager;
    }

    @Override
    public CompletableFuture<Void> downloadFile(FileTransferDto fileTransferDto) throws ExecutionException
            , InterruptedException {
        var localFile = fileTransferDto.getLocal();
        var remoteFile = fileTransferDto.getRemote();
        var downloadRequest = UserFileDownloadRequest.builder()
                .srcFilePath(remoteFile.getFilePath())
                .dstFilePath(localFile.getFilePath())
                .build();
        return requestTemplate(downloadRequest, remoteFile.getIp(), remoteFile.getPort());
    }

    @Override
    public CompletableFuture<Void> uploadFile(FileTransferDto fileTransferDto) throws ExecutionException,
            InterruptedException {
        var localFile = fileTransferDto.getLocal();
        var remoteFile = fileTransferDto.getRemote();
        var uploadRequest = UserFileUploadRequest.builder()
                .srcFilePath(localFile.getFilePath())
                .dstFilePath(remoteFile.getFilePath())
                .build();
        return requestTemplate(uploadRequest, remoteFile.getIp(), remoteFile.getPort());
    }

    private CompletableFuture<Void> requestTemplate(UserRequest request, String ip, int port) throws ExecutionException
            , InterruptedException {
        // Configure channel pipeline
        var pipelineHandlers = pipelineManager.getPipeline();

        // Make a connection
        TcpClient tcpClient = new DefaultTcpClient();
        tcpClient.init(eventLoopGroupManager.channelIo(), pipelineHandlers);
        tcpClient.connect(ip, port).get();

        // Set to close the connection when the response is complete
        request.responseFuture().thenRunAsync(() -> {
            if (tcpClient.isActive()) {
                tcpClient.disconnect();
            }
        });

        // send a request
        tcpClient.send(request);

        // Return an asynchronous result
        return request.responseFuture();
    }
}
