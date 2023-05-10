package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileUploadUserRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserMessage;

import java.util.List;

@Builder
public class FileUploadOutboundRequestProcessor implements OutboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileUploadProcessor fileUploadProcessor;

    @Override
    public List<ProtocolMessage> process(UserMessage message) {
        var userRequest = (FileUploadUserRequest) message;
        return fileUploadProcessor.process(rootPath + userRequest.getSrcPath(), userRequest.getDstPath(), chunkSize);
    }
}
