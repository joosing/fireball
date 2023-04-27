package practice.netty.tcp.client;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StopWatch;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreCompleteListener;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.message.FileFetchRequest;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.FileServiceMessageSpecProvider;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TcpFileClient extends AbstractCustomClient {
    private final EventLoopGroup fileIoGroup;
    private final FileServiceMessageSpecProvider messageSpecProvider;
    private final FileServiceChannelSpecProvider channelSpecProvider; // TODO: 인터페이스로 주입 받도록 개선합시다.

    @Override
    protected void configHandlers(List<HandlerWorkerPair> handlers) {
        // Build up
        List<HandlerWorkerPair> handlerWorkerPairs = List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)),
                HandlerWorkerPair.of(() -> new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new InboundMessageValidator()),
                HandlerWorkerPair.of(fileIoGroup, () -> new FileStoreHandler()), // Dedicated EventLoopGroup
                // Outbound
                HandlerWorkerPair.of(() -> new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())),
                HandlerWorkerPair.of(() -> new OutboundMessageValidator()));

        // Add
        handlers.addAll(handlerWorkerPairs);
    }

    /**
     * 원격 파일 서버에 접근하기 위한 객체 생성
     * @return 원격 파일 서버 접근자
     */
    public RemoteFileAccessor remoteFileAccessor() {
        return new RemoteFileAccessor();
    }

    /**
     * 원격 파일 서버 접근자
     */
    public class RemoteFileAccessor implements FileStoreCompleteListener {
        private String remoteFilePath;
        private String localFilePath;
        private CompletableFuture<Void> transferCompletable;
        private StopWatch stopWatch;
        private String printSpentTimeTag;

        public RemoteFileAccessor remote(String filePath) {
            remoteFilePath = filePath;
            return this;
        }

        public RemoteFileAccessor local(String filePath) {
            localFilePath = filePath;
            return this;
        }

        // 소요 시간 측정
        public RemoteFileAccessor printSpentTime(String tag) {
            stopWatch = new StopWatch();
            printSpentTimeTag = tag;
            return this;
        }

        public CompletableFuture<Void> fetch() {
            if (remoteFilePath == null || localFilePath == null) {
                throw new IllegalArgumentException("remoteFilePath or localFilePath is null");
            }
            // 파일 수신 완료 프로미스 생성
            transferCompletable = new CompletableFuture<>();
            // 파일 수신 완료 이벤트 리스너 등록
            var fileStoreHandler = channel().pipeline().get(FileStoreHandler.class);
            fileStoreHandler.requestStore(localFilePath, this);
            // 파일 전송 요청
            var request = FileFetchRequest.builder()
                    .remoteFilePath(remoteFilePath)
                    .build();
            if (stopWatch != null) {
                stopWatch.start();
            }
            channel().writeAndFlush(request);
            return transferCompletable;
        }

        /**
         * 파일 수신 완료 이벤트 핸들러
         */
        @Override
        public void fileStoreComplete(String localFilePath) {
            transferCompletable.complete(null);
            transferCompletable = null;
            if (stopWatch != null) {
                stopWatch.stop();
                System.out.println(printSpentTimeTag + stopWatch.getTotalTimeSeconds());
                stopWatch = null;
            }
        }
    }
}
