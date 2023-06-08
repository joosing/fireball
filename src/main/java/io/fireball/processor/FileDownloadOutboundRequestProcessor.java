package io.fireball.processor;

import io.fireball.message.FileDownloadRequest;
import io.fireball.message.ProtocolMessage;
import io.fireball.message.UserFileDownloadRequest;
import io.fireball.message.UserRequest;
import lombok.Builder;

import java.util.List;

@Builder
public class FileDownloadOutboundRequestProcessor implements OutboundRequestProcessor {

    @Override
    public List<ProtocolMessage> process(UserRequest message) {
        var userRequest = (UserFileDownloadRequest) message;
        return List.of(FileDownloadRequest.builder()
                .srcFilePath(userRequest.getSrcFile())
                .dstFilePath(userRequest.getDstFile())
                .build());
    }
}
