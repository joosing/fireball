package io.fireball.processor;

import io.fireball.message.ChunkType;
import io.fireball.message.OutboundFileChunk;
import io.fireball.message.ProtocolMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommonFileChunkTransferProcessor implements FileTransferProcessor {

    @Override
    public List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize) throws FileNotFoundException {
        // Check if the source file exists
        if (!Files.exists(Path.of(srcPath))) {
            throw new FileNotFoundException(srcPath);
        }

        // Send an empty chunk to indicate the start of the file
        var result = new ArrayList<ProtocolMessage>();
        var startChunk = new OutboundFileChunk(ChunkType.START_OF_FILE, srcPath, dstPath, 0, 0);
        result.add(startChunk);

        // Send a file content in chunks
        long start = 0;
        var srcFile = new File(srcPath);
        long remainBytes = srcFile.length();
        while (remainBytes > 0) {
            var readBytes = (int) Math.min(remainBytes, chunkSize);
            var chunk = new OutboundFileChunk(ChunkType.FILE_CONTENTS, srcPath, dstPath, start, readBytes);
            result.add(chunk);
            remainBytes -= readBytes;
            start += readBytes;
        }

        // Send an empty chunk to indicate the end of the file
        var lastChunk = new OutboundFileChunk(ChunkType.END_OF_FILE, srcPath, dstPath, start, 0);
        result.add(lastChunk);

        return result;
    }
}
