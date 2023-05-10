package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileDownloadRequest;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.ResponseMessage;
import practice.netty.specification.ResponseCode;

import java.util.List;

@Builder
public class FileDownloadInboundRequestProcessor implements InboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        var recvRequest = (FileDownloadRequest) message;
        var messages = fileTransferProcessor.process(rootPath + recvRequest.getRemoteFilePath(), null, chunkSize);
        var response = new ResponseMessage(ResponseCode.OK);
        messages.add(response);
        return messages;
    }
}
