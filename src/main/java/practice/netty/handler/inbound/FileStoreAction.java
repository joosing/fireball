package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import practice.netty.message.ChunkType;
import practice.netty.message.InboundFileChunk;
import practice.netty.util.AdvancedFileUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@UtilityClass
public class FileStoreAction {
    public static void store(InboundFileChunk response, String targetPath) throws IOException {

        // 파일의 시작 부분이면 파일을 새로 생성
        if (response.chunkType() == ChunkType.START_OF_FILE) {
            AdvancedFileUtils.deleteIfExists(targetPath);
        }

        // 파일에 저장
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetPath, true))) {

            final ByteBuf fileContents = response.fileContents();
            final int requestRead = fileContents.readableBytes();
            if (requestRead != 0) {
                fileContents.readBytes(outputStream, requestRead);
            }
        }
    }
}
