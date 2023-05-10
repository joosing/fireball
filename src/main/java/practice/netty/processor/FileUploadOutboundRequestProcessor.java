package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileUploadRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserMessage;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
@Builder
public class FileUploadOutboundRequestProcessor implements OutboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(UserMessage userRequest) {
        var fileUploadRequest = (UserFileUploadRequest) userRequest;

        // 파일 청크 분할 생성
        var srcPath = rootPath + fileUploadRequest.getSrcPath();
        var dstPath = fileUploadRequest.getDstPath();
        var fileChunks = fileTransferProcessor.process(srcPath, dstPath, chunkSize);

        // 명시적 요청 헤더
        var tailHeader = FileUploadRequest.builder()
                .remoteFilePath(dstPath)
                .build();

        // 종합
        var messages = new ArrayList<ProtocolMessage>();
        messages.addAll(fileChunks);
        messages.add(tailHeader);
        return messages;
    }
}
