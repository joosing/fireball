package practice.netty.specification;

import org.springframework.stereotype.Component;
import practice.netty.message.*;
import practice.netty.processor.*;

@Component
public class MessageSpecProvider implements
        EncodingIdProvider, MessageDecoderProvider, InboundRequestProcessorProvider, OutboundRequestProcessorProvider {

    private final ChannelSpecProvider channelSpec;
    private final EncodingIdManager encodingIdManager;
    private final MessageDecoderManager messageDecoderManager;
    private final InboundRequestProcessorManager inboundRequestProcessorManager;
    private final OutboundRequestProcessorManager outboundRequestProcessorManager;

    public MessageSpecProvider(ChannelSpecProvider channelSpec) {
        this.channelSpec = channelSpec;
        encodingIdManager = new EncodingIdManager();
        messageDecoderManager = new MessageDecoderManager();
        inboundRequestProcessorManager = new InboundRequestProcessorManager();
        outboundRequestProcessorManager = new OutboundRequestProcessorManager();
        configEncodingIdManager();
        configMessageDecoderManager();
        configInboundRequestProcessorManager();
        configTxRequestProcessorManager();
    }

    private void configEncodingIdManager() {
        encodingIdManager.put(FileDownloadRequest.class, 1001);
        encodingIdManager.put(InboundFileChunk.class, 2001);
        encodingIdManager.put(OutboundFileChunk.class, 2001);
        encodingIdManager.put(ResponseMessage.class, 3001);
    }

    private void configMessageDecoderManager() {
        messageDecoderManager.put(1001, FileDownloadRequest::decode);
        messageDecoderManager.put(2001, InboundFileChunk::decode);
        messageDecoderManager.put(3001, ResponseMessage::decode);
    }

    private void configInboundRequestProcessorManager() {
        // 파일 다운로드 요청 수신
        inboundRequestProcessorManager.put(
                FileDownloadRequest.class,
                FileDownloadInboundRequestProcessor.builder()
                        .chunkSize(channelSpec.fileServerSpec().chunkSize())
                        .rootPath(channelSpec.fileServerSpec().rootPath())
                        .fileUploadProcessor(new FileUploadProcessorImpl())
                        .build());

        // 파일 업로드 요청 수신
        inboundRequestProcessorManager.put(
                InboundFileChunk.class,
                FileUploadInboundRequestProcessor.builder()
                        .build());
    }

    private void configTxRequestProcessorManager() {
        // 파일 다운로드 요청 전송 처리
        outboundRequestProcessorManager.put(
                UserFileDownloadRequest.class,
                FileDownloadOutboundRequestProcessor.builder()
                        .build());

        // 파일 업로드 요청 전송 처리
        outboundRequestProcessorManager.put(
                UserFileUploadRequest.class,
                FileUploadOutboundRequestProcessor.builder()
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
    public OutboundRequestProcessor getOutboundRequestProcessor(Class<? extends UserMessage> clazz) {
        return outboundRequestProcessorManager.get(clazz);
    }

    @Override
    public InboundRequestProcessor getInboundRequestProcessor(Class<? extends ProtocolMessage> clazz) {
        return inboundRequestProcessorManager.get(clazz);
    }
}
