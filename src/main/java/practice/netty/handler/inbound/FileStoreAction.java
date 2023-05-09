package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import practice.netty.message.ChunkType;
import practice.netty.message.FileRxChunk;
import practice.netty.util.AdvancedFileUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@UtilityClass
public class FileStoreAction {
    public static void store(FileRxChunk response,
                      String targetPath,
                      @Nullable Runnable storeCompleteAction) throws IOException {
        // 파일의 시작 부분이면 파일을 새로 생성
        if (response.chunkType() == ChunkType.START_OF_FILE) {
            AdvancedFileUtils.deleteIfExists(targetPath);
        }

        // 파일에 저장
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetPath, true))) {

            final ByteBuf fileContents = response.fileContents();
            final int requestRead = fileContents.readableBytes();
            if (requestRead != 0) {
                fileContents.readBytes(outputStream, requestRead);
            }
        }

        // 파일의 끝 부분이면 저장 완료 알림
        if (response.chunkType() == ChunkType.END_OF_FILE) {
            // 저장 완료 알림
            if (storeCompleteAction!= null) {
                storeCompleteAction.run();
            }
        }
    }
}
