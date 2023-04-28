package practice.netty.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;
import practice.netty.handler.inbound.FileDownloadCompleteListener;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.message.FileDownloadRequest;
import practice.netty.specification.ChannelSpecProvider;
import practice.netty.specification.MessageSpecProvider;
import practice.netty.tcp.client.DefaultTcpClient;
import practice.netty.tcp.client.TcpClient;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TcpFileClient implements FileClient {
    private final MessageSpecProvider messageSpecProvider;
    private final ChannelSpecProvider channelSpecProvider;
    private volatile StopWatch stopWatch;
    @Override
    public CompletableFuture<Void> downloadFile(RemoteFile remoteFile, LocalFile localFile) throws ExecutionException
            , InterruptedException {
        // 이벤트루프 그룹 생성
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup fileIoEventLoopGroup = new NioEventLoopGroup();

        stopWatch = new StopWatch();

        // 파일 다운로드 완료 이벤트 처리 준비
        var downloadCompletable = new CompletableFuture<Void>();
        var fileDownloadCompleteHandler = (FileDownloadCompleteListener) localFilePath -> {
            stopWatch.stop();
            log.info("File download time {} sec", stopWatch.getTotalTimeSeconds());
            downloadCompletable.complete(null);
            eventLoopGroup.shutdownGracefully();
            fileIoEventLoopGroup.shutdownGracefully();
        };

        // 채널 파이프라인 구성
        var handlers = new ArrayList<>(List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(fileIoEventLoopGroup, () -> new FileStoreHandler(localFile.getPath(), fileDownloadCompleteHandler)), // Dedicated EventLoopGroup
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.headerSpec())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator())));

        // TCP 클라이언트 생성 및 초기화
        TcpClient tcpClient = new DefaultTcpClient();
        tcpClient.init(eventLoopGroup, handlers);
        tcpClient.connect(remoteFile.getIp(), remoteFile.getPort()).get();

        // 파일 다운로드 요청
        var request = FileDownloadRequest.builder()
                .remoteFilePath(remoteFile.getPath())
                .build();
        stopWatch.start();
        tcpClient.send(request);
        return downloadCompletable;
    }

    @Override
    public CompletableFuture<Void> uploadFile(LocalFile localFilePath, RemoteFile remoteFile) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
