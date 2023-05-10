package practice.netty.processor;

import practice.netty.message.ProtocolMessage;

import java.util.List;

@FunctionalInterface
public interface FileTransferProcessor {
    List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize);
}
