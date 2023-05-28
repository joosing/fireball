package practice.netty.client;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.netty.common.HandlerWorkerPair;
import practice.netty.dto.FileTransferDto;
import practice.netty.eventloop.ClientEventLoopGroupManager;
import practice.netty.handler.inbound.CompleteResponseNotifier;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.handler.outbound.UserRequestHandler;
import practice.netty.message.UserFileDownloadRequest;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserRequest;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class TcpFileClient implements FileClient {
    private final ClientEventLoopGroupManager eventLoopGroupManager;
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;

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
    public CompletableFuture<Void> uploadFile(FileTransferDto fileTransferDto) throws ExecutionException, InterruptedException {
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
        var pipelineHandlers = defineChannelPipeline();

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

    private List<HandlerWorkerPair> defineChannelPipeline() {
        return new ArrayList<>(List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(eventLoopGroupManager.fireStore(), () -> new FileStoreHandler(channelSpecProvider.client().rootPath())), // Dedicated EventLoopGroup
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                HandlerWorkerPair.of(() -> new UserRequestHandler(messageSpecProvider)),
                // Duplex
                HandlerWorkerPair.of(() -> new CompleteResponseNotifier())));
    }
}
