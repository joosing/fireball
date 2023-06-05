package io.fireball.processor;

import io.fireball.message.FileUploadRequest;
import io.fireball.message.ProtocolMessage;
import io.fireball.message.UserFileUploadRequest;
import io.fireball.message.UserRequest;
import lombok.Builder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
@Builder
public class FileUploadOutboundRequestProcessor implements OutboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(UserRequest message) throws Exception {
        var uploadRequest = (UserFileUploadRequest) message;

        // 파일 청크 분할 생성
        var srcFilePath = Path.of(rootPath, uploadRequest.getSrcFilePath()).normalize().toString();
        var dstFilePath = uploadRequest.getDstFilePath();
        var fileChunks = fileTransferProcessor.process(srcFilePath, dstFilePath, chunkSize);

        // 명시적 요청 헤더
        var tailHeader = FileUploadRequest.builder()
                .srcFilePath(srcFilePath)
                .dstFilePath(dstFilePath)
                .build();

        // 종합
        var messages = new ArrayList<ProtocolMessage>();
        messages.addAll(fileChunks);
        messages.add(tailHeader);
        return messages;
    }
}
