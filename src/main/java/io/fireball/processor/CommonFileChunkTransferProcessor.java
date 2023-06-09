package io.fireball.processor;

import io.fireball.exception.NotFileException;
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

    private static void validate(String srcPath) throws Exception {
        if (Files.isDirectory(Path.of(srcPath))) {
            throw new NotFileException(srcPath);
        }
        if (!Files.exists(Path.of(srcPath))) {
            throw new FileNotFoundException(srcPath);
        }
    }

    @Override
    public List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize) throws Exception {
        var messages = new ArrayList<ProtocolMessage>();

        // Validate
        validate(srcPath);

        // Start of file
        var startChunk = new OutboundFileChunk(ChunkType.START_OF_FILE, srcPath, dstPath, 0, 0);
        messages.add(startChunk);

        // Content in chunks
        long start = 0;
        var srcFile = new File(srcPath);
        long remainBytes = srcFile.length();
        while (remainBytes > 0) {
            var readBytes = (int) Math.min(remainBytes, chunkSize);
            var chunk = new OutboundFileChunk(ChunkType.MIDDLE_OF_FILE, srcPath, dstPath, start, readBytes);
            messages.add(chunk);
            remainBytes -= readBytes;
            start += readBytes;
        }

        // End of file
        var lastChunk = new OutboundFileChunk(ChunkType.END_OF_FILE, srcPath, dstPath, start, 0);
        messages.add(lastChunk);

        return messages;
    }
}
