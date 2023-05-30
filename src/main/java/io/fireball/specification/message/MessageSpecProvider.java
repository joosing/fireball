package io.fireball.specification.message;

import io.fireball.message.*;
import io.fireball.processor.*;
import io.fireball.specification.channel.ChannelSpecProvider;
import org.springframework.stereotype.Component;

/**
 * 메시지 처리에 필요한 함수와 데이터를 제공합니다.
 */
@Component
public class MessageSpecProvider implements
        ProtocolIdProvider, MessageDecoderProvider, InboundRequestProcessorProvider, OutboundRequestProcessorProvider {

    private final ChannelSpecProvider channelSpec; // 채널 관련 스펙
    private final ProtocolIdManager protocolIdManager; // 프로토콜 ID 관리
    private final MessageDecoderManager messageDecoderManager; // 메시지 디코더 함수 관리
    private final InboundRequestProcessorManager inboundRequestProcessorManager; // 인바운드 요청 처리기 함수 관리
    private final OutboundRequestProcessorManager outboundRequestProcessorManager; // 아웃바운드 요청 처리기 함수 관리

    public MessageSpecProvider(ChannelSpecProvider channelSpec) {
        this.channelSpec = channelSpec;
        protocolIdManager = new ProtocolIdManager();
        messageDecoderManager = new MessageDecoderManager();
        inboundRequestProcessorManager = new InboundRequestProcessorManager();
        outboundRequestProcessorManager = new OutboundRequestProcessorManager();
        configureProtocolIdManager();
        configureMessageDecoderManager();
        configureInboundRequestProcessorManager();
        configureOutboundRequestProcessorManager();
    }

    /**
     * 프로토콜 ID 관리자를 구성합니다.
     */
    private void configureProtocolIdManager() {
        protocolIdManager.put(FileDownloadRequest.class, 1001);
        protocolIdManager.put(FileUploadRequest.class, 1002);
        protocolIdManager.put(InboundFileChunk.class, 2001);
        protocolIdManager.put(OutboundFileChunk.class, 2001);
        protocolIdManager.put(ResponseMessage.class, 3001);
    }

    /**
     * 메시지 디코더 관리자를 구성합니다.
     */
    private void configureMessageDecoderManager() {
        messageDecoderManager.put(1001, FileDownloadRequest::decode);
        messageDecoderManager.put(1002, FileUploadRequest::decode);
        messageDecoderManager.put(2001, InboundFileChunk::decode);
        messageDecoderManager.put(3001, ResponseMessage::decode);
    }

    /**
     * 인바운드 요청 처리기 관리자를 구성합니다.
     */
    private void configureInboundRequestProcessorManager() {
        // 파일 다운로드 요청 수신
        inboundRequestProcessorManager.put(
                FileDownloadRequest.class,
                FileDownloadInboundRequestProcessor.builder()
                        .chunkSize(channelSpec.server().chunkSize())
                        .rootPath(channelSpec.server().rootPath())
                        .fileTransferProcessor(new CommonFileChunkTransferProcessor())
                        .build());

        // 파일 업로드 요청 수신
        inboundRequestProcessorManager.put(
                FileUploadRequest.class,
                EmptyBodyRetrieveProcessor.INSTANCE);
    }

    /**
     * 아웃바운드 요청 처리기 관리자를 구성합니다.
     */
    private void configureOutboundRequestProcessorManager() {
        // 파일 다운로드 요청 전송 처리
        outboundRequestProcessorManager.put(
                UserFileDownloadRequest.class,
                FileDownloadOutboundRequestProcessor.builder()
                        .build());

        // 파일 업로드 요청 전송 처리
        outboundRequestProcessorManager.put(
                UserFileUploadRequest.class,
                FileUploadOutboundRequestProcessor.builder()
                        .chunkSize(channelSpec.server().chunkSize())
                        .rootPath(channelSpec.server().rootPath())
                        .fileTransferProcessor(new CommonFileChunkTransferProcessor())
                        .build());
    }

    /**
     * 인코딩될 수 있는 메시지 클래스 타입을 받아 프로토콜 ID를 반환합니다.
     * @param clazz 인코딩될 수 있는 메시지 클래스 타입
     * @return 프로토콜 ID
     */
    @Override
    public int getProtocolId(Class<? extends MessageEncodable> clazz) {
        return protocolIdManager.get(clazz);
    }

    /**
     * 프로토콜 ID를 받아 메시지 디코더 함수를 반환합니다.
     * @param id 프로토콜 ID
     * @return 메시지 디코더 함수
     */
    @Override
    public DecodeFunction getDecoder(int id) {
        return messageDecoderManager.get(id);
    }

    /**
     * 사용자의 요청 메시지 클래스 타입을 받아 아웃바운드 요청 처리기 함수를 반환합니다.
     * @param clazz 사용자의 요청 메시지 클래스 타입
     * @return 아웃바운드 요청 처리기 함수
     */
    @Override
    public OutboundRequestProcessor getOutboundRequestProcessor(Class<? extends UserRequest> clazz) {
        return outboundRequestProcessorManager.get(clazz);
    }

    /**
     * 프로토콜 메시지 클래스 타입을 받아 인바운드 요청 처리기 함수를 반환합니다.
     * @param clazz 프로토콜 메시지 클래스 타입
     * @return 인바운드 요청 처리기 함수
     */
    @Override
    public InboundRequestProcessor getInboundRequestProcessor(Class<? extends ProtocolMessage> clazz) {
        return inboundRequestProcessorManager.get(clazz);
    }
}
