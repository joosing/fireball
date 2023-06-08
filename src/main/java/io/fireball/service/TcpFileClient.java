package io.fireball.service;

import io.fireball.dto.FileDownloadDto;
import io.fireball.dto.FileUploadDto;
import io.fireball.eventloop.ClientEventLoopGroupManager;
import io.fireball.handler.duplex.RequestResultChecker;
import io.fireball.message.UserFileDownloadRequest;
import io.fireball.message.UserFileUploadRequest;
import io.fireball.message.UserRequest;
import io.fireball.pipeline.PipelineFactory;
import io.fireball.tcp.client.DefaultTcpClient;
import io.fireball.tcp.client.TcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    public void downloadFile(FileDownloadDto spec) throws ExecutionException
            , InterruptedException, TimeoutException {
        var downloadRequest = UserFileDownloadRequest.builder()
                .srcFile(spec.source().file())
                .dstFile(spec.destination().file())
                .build();
        requestTemplate(downloadRequest, spec.source().ip(), spec.source().port());
    }

    @Override
    public void uploadFile(FileUploadDto spec) throws ExecutionException,
            InterruptedException, TimeoutException {
        var uploadRequest = UserFileUploadRequest.builder()
                .srcFile(spec.source().file())
                .dstFile(spec.destination().file())
                .build();
        requestTemplate(uploadRequest, spec.destination().ip(), spec.destination().port());
    }

    private void requestTemplate(UserRequest request, String ip, int port) throws ExecutionException
            , InterruptedException, TimeoutException {
        // Configure channel pipeline
        var pipelineFactory = this.pipelineFactory.get();

        // Make a connection
        TcpClient tcpClient = new DefaultTcpClient();
        tcpClient.init(eventLoopGroupManager.channelIo(), pipelineFactory);
        tcpClient.connect(ip, port).get();

        // Get a response future
        var future = tcpClient.pipeline().get(RequestResultChecker.class).completableFuture();

        // Send a request
        tcpClient.send(request).addListener(f -> {
            if (!f.isSuccess()) {
                future.completeExceptionally(f.cause());
            }
        });

        // Wait for the response
        try {
            future.get();
        } finally {
            tcpClient.disconnect();
        }
    }
}
