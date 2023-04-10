package practice.netty.tcp.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import practice.netty.handler.inbound.FileServiceDecoder;
import practice.netty.handler.inbound.FileStoreCompleteListener;
import practice.netty.handler.inbound.FileStoreHandler;
import practice.netty.handler.inbound.InboundMessageValidator;
import practice.netty.handler.outbound.FileServiceEncoder;
import practice.netty.handler.outbound.OutboundMessageValidator;
import practice.netty.message.FileFetchRequest;
import practice.netty.specification.FileServiceChannelSpecProvider;
import practice.netty.specification.MessageSpecProvider;
import practice.netty.tcp.common.Handler;

import java.util.List;

@RequiredArgsConstructor
public class TcpFileClient extends AbstractCustomClient {
    private final EventLoopGroup fileIoGroup;
    private final MessageSpecProvider messageSpecProvider;
    private final FileServiceChannelSpecProvider channelSpecProvider; // TODO: 인터페이스로 주입 받도록 개선합시다.
    private FileStoreHandler fileStoreHandler;

    @Override
    protected void configHandlers(List<Handler> handlers) {
        fileStoreHandler = new FileStoreHandler();

        // Inbound
        // handlers.add(Handler.of(new LoggingHandler(LogLevel.INFO))); // for debugging
        handlers.add(Handler.of(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)));
        handlers.add(Handler.of(new FileServiceDecoder(messageSpecProvider, channelSpecProvider.header())));
        handlers.add(Handler.of(new InboundMessageValidator()));
        handlers.add(Handler.of(fileIoGroup, fileStoreHandler));
        // Outbound
        handlers.add(Handler.of(new FileServiceEncoder(messageSpecProvider, channelSpecProvider.header())));
        handlers.add(Handler.of(new OutboundMessageValidator()));
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
        private ChannelPromise transferPromise;

        public RemoteFileAccessor remote(String filePath) {
            remoteFilePath = filePath;
            return this;
        }

        public RemoteFileAccessor local(String filePath) {
            localFilePath = filePath;
            return this;
        }

        public ChannelFuture fetch() {
            if (remoteFilePath == null || localFilePath == null) {
                throw new IllegalArgumentException("remoteFilePath or localFilePath is null");
            }
            // 파일 수신 완료 프로미스 생성
            transferPromise = channel().newPromise();
            // 파일 수신 완료 이벤트 리스너 등록
            fileStoreHandler.requestStore(localFilePath, this);
            // 파일 전송 요청
            FileFetchRequest request = FileFetchRequest.builder()
                    .remoteFilePath(remoteFilePath)
                    .build();
            channel().writeAndFlush(request);
            return transferPromise;
        }

        /**
         * 파일 수신 완료 이벤트 핸들러
         */
        @Override
        public void fileStoreComplete(String localFilePath) {
            transferPromise.setSuccess();
        }
    }
}
