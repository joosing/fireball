package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.FileDownloadRequest;
import practice.netty.message.ProtocolMessage;

import java.util.List;

@Builder
public class FileDownloadInboundRequestProcessor implements InboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        var request = (FileDownloadRequest) message;
        var srcPath = rootPath + request.getRemoteFilePath();
        return fileTransferProcessor.process(srcPath, null, chunkSize);
    }
}
