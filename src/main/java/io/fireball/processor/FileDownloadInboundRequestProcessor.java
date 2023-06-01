package io.fireball.processor;

import io.fireball.message.FileDownloadRequest;
import io.fireball.message.ProtocolMessage;
import lombok.Builder;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

@Builder
public class FileDownloadInboundRequestProcessor implements InboundRequestProcessor {
    private final int chunkSize;
    private final String rootPath;
    private final FileTransferProcessor fileTransferProcessor;

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) throws FileNotFoundException {
        var request = (FileDownloadRequest) message;
        var srcFilePath = Path.of(rootPath, request.getSrcFilePath()).normalize().toString();
        var dstFilePath = request.getDstFilePath();
        return fileTransferProcessor.process(srcFilePath, dstFilePath, chunkSize);
    }
}
