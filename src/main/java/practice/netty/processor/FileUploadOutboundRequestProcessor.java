package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserFileUploadRequest;
import practice.netty.message.UserMessage;

import java.util.List;

@Builder
public class FileUploadOutboundRequestProcessor implements OutboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(UserMessage message) {
        var userRequest = (UserFileUploadRequest) message;
        return fileTransferProcessor.process(rootPath + userRequest.getSrcPath(), userRequest.getDstPath(), chunkSize);
    }
}
