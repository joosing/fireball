package practice.netty.specification;

import org.springframework.stereotype.Component;
import practice.netty.message.*;
import practice.netty.processor.*;

@Component
public class MessageSpecProvider implements
        EncodingIdProvider, MessageDecoderProvider, RxRequestProcessorProvider, TxRequestProcessorProvider {

    private final ChannelSpecProvider channelSpec;
    private final EncodingIdManager encodingIdManager;
    private final MessageDecoderManager messageDecoderManager;
    private final RxRequestProcessorManager rxRequestProcessorManager;
    private final TxRequestProcessorManager txRequestProcessorManager;

    public MessageSpecProvider(ChannelSpecProvider channelSpec) {
        this.channelSpec = channelSpec;
        encodingIdManager = new EncodingIdManager();
        messageDecoderManager = new MessageDecoderManager();
        rxRequestProcessorManager = new RxRequestProcessorManager();
        txRequestProcessorManager = new TxRequestProcessorManager();
        configEncodingIdManager();
        configMessageDecoderManager();
        configRxRequestProcessorManager();
        configTxRequestProcessorManager();
    }

    private void configEncodingIdManager() {
        encodingIdManager.put(FileDownloadProtocolRequest.class, 1001);
        encodingIdManager.put(FileRxChunk.class, 2001);
        encodingIdManager.put(FileTxChunk.class, 2001);
        encodingIdManager.put(ResponseMessage.class, 3001);
    }

    private void configMessageDecoderManager() {
        messageDecoderManager.put(1001, FileDownloadProtocolRequest::decode);
        messageDecoderManager.put(2001, FileRxChunk::decode);
        messageDecoderManager.put(3001, ResponseMessage::decode);
    }

    private void configRxRequestProcessorManager() {
        // 파일 다운로드 요청 수신
        rxRequestProcessorManager.put(
                FileDownloadProtocolRequest.class,
                FileDownloadRxRequestProcessor.builder()
                        .chunkSize(channelSpec.fileServerSpec().chunkSize())
                        .rootPath(channelSpec.fileServerSpec().rootPath())
                        .fileUploadProcessor(new FileUploadProcessorImpl())
                        .build());

        // 파일 업로드 요청 수신
        rxRequestProcessorManager.put(
                FileRxChunk.class,
                FileUploadRxRequestProcessor.builder()
                        .build());
    }

    private void configTxRequestProcessorManager() {
        // 파일 다운로드 요청 전송 처리
        txRequestProcessorManager.put(
                FileDownloadUserRequest.class,
                FileDownloadTxRequestProcessor.builder()
                        .build());

        // 파일 업로드 요청 전송 처리
        txRequestProcessorManager.put(
                FileUploadUserRequest.class,
                FileUploadTxRequestProcessor.builder()
                        .chunkSize(channelSpec.fileServerSpec().chunkSize())
                        .rootPath(channelSpec.fileServerSpec().rootPath())
                        .fileUploadProcessor(new FileUploadProcessorImpl())
                        .build());
    }

    @Override
    public int getId(Class<? extends MessageEncodable> clazz) {
        return encodingIdManager.get(clazz);
    }

    @Override
    public DecodeFunction getDecoder(int id) {
        return messageDecoderManager.get(id);
    }

    @Override
    public TxRequestProcessor getTxRequestProcessor(Class<? extends UserMessage> clazz) {
        return txRequestProcessorManager.get(clazz);
    }

    @Override
    public RxRequestProcessor getRxRequestProcessor(Class<? extends ProtocolMessage> clazz) {
        return rxRequestProcessorManager.get(clazz);
    }
}
