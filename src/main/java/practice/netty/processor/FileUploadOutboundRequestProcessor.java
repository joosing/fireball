package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileUploadRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
@Builder
public class FileUploadOutboundRequestProcessor implements OutboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(UserRequest message) throws FileNotFoundException {
        var uploadRequest = (UserFileUploadRequest) message;

        // 파일 청크 분할 생성
        var srcFilePath = rootPath + uploadRequest.getSrcFilePath();
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
