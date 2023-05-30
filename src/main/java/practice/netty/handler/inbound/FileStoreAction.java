package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import practice.netty.message.ChunkType;
import practice.netty.message.InboundFileChunk;
import practice.netty.util.AdvancedFileUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@UtilityClass
public class FileStoreAction {
    public static void store(InboundFileChunk chunk, String targetPath) throws IOException {

        // Delete an existing file
        if (chunk.type() == ChunkType.START_OF_FILE) {
            AdvancedFileUtils.deleteIfExists(targetPath);
        }

        final ByteBuf chunkContents = chunk.contents();
        final int nBytesToStore = chunkContents.readableBytes();

        // Store chunk contents
        if (nBytesToStore != 0) {
            try (var fileOutputStream = new FileOutputStream(targetPath, true);
                 var bufferedOutputStream = new BufferedOutputStream(fileOutputStream, nBytesToStore)) {
                chunkContents.readBytes(bufferedOutputStream, nBytesToStore);
            }
        }
    }
}
