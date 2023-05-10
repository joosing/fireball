package practice.netty.client;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;
import practice.netty.handler.inbound.FileClientStoreHandler;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.ResponseSupplier;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.handler.outbound.UserRequestHandler;
import practice.netty.message.FileDownloadRequest;
import practice.netty.message.ResponseMessage;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.specification.ChannelSpecProvider;
import practice.netty.specification.MessageSpecProvider;
import practice.netty.specification.ResponseCode;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TcpFileClient implements FileClient {
    // 이벤트루프 그룹 생성
    private final EventLoopGroup clientEventLoopGroup;
    private final EventLoopGroup fileStoreEventLoopGroup;
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;

    @Override
    public CompletableFuture<Void> downloadFile(RemoteFile remoteFile, LocalFile localFile) throws ExecutionException
            , InterruptedException {
        // TCP 클라이언트 생성
        TcpClient tcpClient = new DefaultTcpClient();

        // 파일 다운로드 완료 이벤트 처리 준비
        var downloadCompletable = new CompletableFuture<Void>();
        var downloadCompleteAction = (Runnable) () -> {
            if (tcpClient.isActive()) {
                tcpClient.disconnect();
            }
            downloadCompletable.complete(null);
        };

        // 채널 파이프라인 구성
        var handlers = new ArrayList<>(List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(fileStoreEventLoopGroup, () -> new FileClientStoreHandler(localFile.getPath(), downloadCompleteAction)), // Dedicated EventLoopGroup
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator())));

        // TCP 클라이언트 생성 및 초기화
        tcpClient.init(clientEventLoopGroup, handlers);
        tcpClient.connect(remoteFile.getIp(), remoteFile.getPort()).get();

        // 파일 다운로드 요청
        var request = FileDownloadRequest.builder()
                .remoteFilePath(remoteFile.getPath())
                .build();
        tcpClient.send(request);

        // 비동기 결과 반환
        return downloadCompletable;
    }

    @Override
    public CompletableFuture<Void> uploadFile(LocalFile localFilePath, RemoteFile remoteFile) throws ExecutionException, InterruptedException {
        // TCP 클라이언트 생성
        TcpClient tcpClient = new DefaultTcpClient();

        // 파일 업로드 완료 이벤트 처리 준비
        var uploadCompletable = new CompletableFuture<Void>();
        var uploadCompleteAction = (Consumer<ResponseMessage>) response -> {
            if (tcpClient.isActive()) {
                tcpClient.disconnect();
            }
            if (response.responseCode() == ResponseCode.OK) {
                uploadCompletable.complete(null);
            } else {
                var errorMessage = response.responseCode().getMessage();
                uploadCompletable.completeExceptionally(new RuntimeException(errorMessage));
            }
        };

        // 채널 파이프라인 구성
        var handlers = new ArrayList<>(List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(() -> new ResponseSupplier(uploadCompleteAction)),
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                HandlerWorkerPair.of(() -> new UserRequestHandler(messageSpecProvider))));

        // TCP 클라이언트 생성 및 연결
        tcpClient.init(clientEventLoopGroup, handlers);
        tcpClient.connect(remoteFile.getIp(), remoteFile.getPort()).get();

        // 파일 업로드 요청
        var request = UserFileUploadRequest.builder()
                .srcPath(localFilePath.getPath())
                .dstPath(remoteFile.getPath())
                .build();
        tcpClient.send(request);

        // 비동기 결과 반환
        return uploadCompletable;
    }
}
