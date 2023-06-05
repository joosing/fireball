package io.fireball.processor;

import io.fireball.message.ProtocolMessage;

import java.util.List;

@FunctionalInterface
public interface FileTransferProcessor {
    List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize) throws Exception;
}
