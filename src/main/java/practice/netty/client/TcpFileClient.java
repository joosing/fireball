package practice.netty.client;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.netty.common.HandlerWorkerPair;
import practice.netty.configuration.ClientEventLoopGroupConfig;
import practice.netty.dto.FileTransferDto;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.inbound.ResponseSupplier;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.handler.outbound.UserRequestHandler;
import practice.netty.message.ResponseMessage;
import practice.netty.message.UserFileDownloadRequest;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserMessage;
import practice.netty.specification.channel.ChannelSpecProvider;
import practice.netty.specification.message.MessageSpecProvider;
import practice.netty.specification.response.ResponseCode;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TcpFileClient implements FileClient {
    private final ClientEventLoopGroupConfig eventLoopGroupConfig;
    private final MessageSpecProvider messageSpecProvider; // 메시지 스펙
    private final ChannelSpecProvider channelSpecProvider; // 채널 스펙
    private EventLoopGroup clientEventLoopGroup; // 클라이언트 I/O 스레드 그룹
    private EventLoopGroup fileStoreEventLoopGroup; // 파일 저장 전용 스레드 그룹

    @PostConstruct
    public void setUp() {
        clientEventLoopGroup = eventLoopGroupConfig.channelIoEventLoopGroup();
        fileStoreEventLoopGroup = eventLoopGroupConfig.fileStoreEventLoopGroup();
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
    public CompletableFuture<Void> uploadFile(FileTransferDto fileTransferDto) throws ExecutionException, InterruptedException {
        var localFile = fileTransferDto.getLocal();
        var remoteFile = fileTransferDto.getRemote();
        var uploadRequest = UserFileUploadRequest.builder()
                .srcFilePath(localFile.getFilePath())
                .dstFilePath(remoteFile.getFilePath())
                .build();
        return requestTemplate(uploadRequest, remoteFile.getIp(), remoteFile.getPort());
    }

    private CompletableFuture<Void> requestTemplate(UserMessage request, String ip, int port) throws ExecutionException
            , InterruptedException {
        // TCP 클라이언트 생성
        TcpClient tcpClient = new DefaultTcpClient();

        // 처리 완료 이벤트 준비
        var completableFuture = new CompletableFuture<Void>();
        var responseAction = defineResponseAction(tcpClient, completableFuture);

        // 채널 파이프라인 핸들러 구성
        var pipelineHandlers = defineChannelPipeline(responseAction);

        // TCP 클라이언트 생성 및 초기화
        tcpClient.init(clientEventLoopGroup, pipelineHandlers);
        tcpClient.connect(ip, port).get();

        // 요청 전송
        tcpClient.send(request);

        // 비동기 결과 반환
        return completableFuture;
    }

    private List<HandlerWorkerPair> defineChannelPipeline(Consumer<ResponseMessage> transferCompleteAction) {
        return new ArrayList<>(List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(fileStoreEventLoopGroup, () -> new FileStoreHandler(channelSpecProvider.client().rootPath())), // Dedicated EventLoopGroup
                HandlerWorkerPair.of(() -> new ResponseSupplier(transferCompleteAction)),

                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()),
                HandlerWorkerPair.of(() -> new UserRequestHandler(messageSpecProvider))));
    }

    private static Consumer<ResponseMessage> defineResponseAction(TcpClient tcpClient, CompletableFuture<Void> completeFuture) {

        return response -> {
            if (tcpClient.isActive()) {
                tcpClient.disconnect();
            }
            if (response.responseCode() == ResponseCode.OK) {
                completeFuture.complete(null);
            } else {
                var errorMessage = response.responseCode().getMessage();
                completeFuture.completeExceptionally(new RuntimeException(errorMessage));
            }
        };
    }
}
