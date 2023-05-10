package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileDownloadRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserFileDownloadRequest;
import practice.netty.message.UserMessage;

import java.util.List;

@Builder
public class FileDownloadOutboundRequestProcessor implements OutboundRequestProcessor {

    @Override
    public List<ProtocolMessage> process(UserMessage message) {
        var userRequest = (UserFileDownloadRequest) message;
        return List.of(FileDownloadRequest.builder()
                .remoteFilePath(userRequest.getRemoteFilePath())
                .build());
    }
}
