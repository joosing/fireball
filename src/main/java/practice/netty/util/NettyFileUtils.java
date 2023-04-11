package practice.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.experimental.UtilityClass;

import java.io.*;

@UtilityClass
public class NettyFileUtils {
    /**
     * 파일의 전체 내용을 ByteBuf에 담아 반환합니다.
     * @param filePath 파일 경로
     * @param allocator ByteBuf 할당자
     * @return 파일 전체 내용을 담은 ByteBuf
     */
    public static ByteBuf readAllBytes(String filePath, ByteBufAllocator allocator) throws IOException {
        var file = new File(filePath);
        checkNettyByteBufCapacity(file.length());
        var buffer = allocator.buffer((int) file.length());
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            buffer.writeBytes(inputStream, (int) file.length());
        }
        return buffer;
    }

    private static void checkNettyByteBufCapacity(long size) {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large file: " + size + " bytes");
        }
    }
}
