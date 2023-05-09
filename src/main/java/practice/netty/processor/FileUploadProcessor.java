package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.util.List;

@FunctionalInterface
public interface FileUploadProcessor {
    List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize);
}
