package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileDownloadProtocolRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.ResponseMessage;
import practice.netty.specification.ResponseCode;

import java.util.List;

@Builder
public class FileDownloadInboundRequestProcessor implements InboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileUploadProcessor fileUploadProcessor;

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        var recvRequest = (FileDownloadProtocolRequest) message;
        var messages = fileUploadProcessor.process(rootPath + recvRequest.getRemoteFilePath(), null, chunkSize);
        var response = new ResponseMessage(ResponseCode.OK);
        messages.add(response);
        return messages;
    }
}
