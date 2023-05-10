package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileDownloadProtocolRequest;
import practice.netty.message.FileDownloadUserRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.UserMessage;

import java.util.List;

@Builder
public class FileDownloadOutboundRequestProcessor implements OutboundRequestProcessor {

    @Override
    public List<ProtocolMessage> process(UserMessage message) {
        var userRequest = (FileDownloadUserRequest) message;
        return List.of(FileDownloadProtocolRequest.builder()
                .remoteFilePath(userRequest.getRemoteFilePath())
                .build());
    }
}
